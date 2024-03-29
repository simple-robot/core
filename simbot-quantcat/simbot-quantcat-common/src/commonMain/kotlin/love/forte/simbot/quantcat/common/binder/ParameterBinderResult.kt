package love.forte.simbot.quantcat.common.binder

import love.forte.simbot.common.PriorityConstant
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * `ParameterBinderFactory` 的解析处理结果返回值。
 */
public sealed class ParameterBinderResult {
    /**
     * binder
     */
    public abstract val binder: ParameterBinder?

    /**
     * 优先级
     */
    public open val priority: Int = PriorityConstant.NORMAL

    public companion object {
        @JvmStatic
        public fun empty(): Empty = Empty

        @JvmStatic
        @JvmOverloads
        public fun normal(binder: ParameterBinder, priority: Int = PriorityConstant.NORMAL): NotEmpty =
            Normal(binder, priority)

        @JvmStatic
        @JvmOverloads
        public fun only(binder: ParameterBinder, priority: Int = PriorityConstant.NORMAL): NotEmpty =
            Only(binder, priority)

        @JvmStatic
        @JvmOverloads
        public fun spare(binder: ParameterBinder, priority: Int = PriorityConstant.NORMAL): NotEmpty =
            Spare(binder, priority)

    }

    /**
     * 没有结果. 此结果应当被抛弃.
     */
    public data object Empty : ParameterBinderResult() {
        override val binder: ParameterBinder? get() = null
    }

    /**
     * 存在结果的result。
     */
    public sealed class NotEmpty : ParameterBinderResult() {
        abstract override val binder: ParameterBinder
    }


    /**
     * 基础的结果，会作为所有binder集合中的一员。
     */
    public class Normal internal constructor(
        override val binder: ParameterBinder,
        override val priority: Int,
    ) : NotEmpty()

    /**
     * 唯一的结果。当返回此结果的时候，会抛弃此结果之前的所有结果，并仅保留此结果。
     * 并且直到遇到下一个 [Only] 之前，不会追加其他结果。当遇到下一个 [Only] 后，当前的唯一结果将会被替换。
     */
    public class Only internal constructor(
        override val binder: ParameterBinder,
        override val priority: Int,
    ) : NotEmpty()

    /**
     * 备用的结果。当其他结果没有任何执行成功的结果时才会使用 [Spare] 中所提供的内容进行尝试。
     * 不会与 [Only] 发生冲突，因此 [Spare] binder 与普通binder是分离的。
     *
     * [Spare] 同样可以存在多个，但是不存在 `Only` 的备用binder。
     *
     */
    public class Spare internal constructor(
        override val binder: ParameterBinder,
        override val priority: Int,
    ) : NotEmpty()
}

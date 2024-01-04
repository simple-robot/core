package love.forte.simbot.ability

import love.forte.simbot.suspendrunner.JST
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmSynthetic


/**
 * 对“删除行为”的支持。
 *
 * @author ForteScarlet
 */
@JST
public interface DeleteSupport {

    /**
     * 进行删除行为。
     *
     * 在实现者本质上不支持删除行为时，抛出 [UnsupportedOperationException]。
     *
     * [delete] 行为如果遇到诸如权限不足、已被删除等API支持、但由于逻辑、业务原因而导致删除失败的情况下，都应抛出异常，
     * 且议使用或扩展 [DeleteFailureException] 类型。
     *
     * “删除行为”的具体含义由实现者定义。例如在 `MessageContent` 中，代表删除、撤回、撤销这个消息。
     *
     * @param options 删除时的可选选项。不支持的选项将会被忽略。更多说明参考 [DeleteOption]。
     *
     * @throws UnsupportedOperationException 如果 [DeleteSupport] 被默认实现但实现者不支持此API时。
     * @throws DeleteFailureException 删除行为失败异常。
     * @throws NoSuchElementException 如果因目标缺失而删除失败
     * @throws Exception 可能产生任何异常。
     */
    public suspend fun delete(vararg options: DeleteOption)
}

/**
 * [DeleteSupport.delete] 的可选选项。
 *
 * [DeleteOption] 可以自由扩展，且如果遇到不支持的实现则会将其忽略。
 * 但是所有 [DeleteSupport.delete] 都应当尽可能支持 [StandardDeleteOption]
 * 中提供的标准选项，并在不支持某些标准选项的时候提供相关的说明。
 *
 * @see StandardDeleteOption
 */
public interface DeleteOption

/**
 * [DeleteOption] 的标准选项实现。
 */
public enum class StandardDeleteOption : DeleteOption {
    /**
     * 如果是因为缺失目标而导致的删除失败，不抛出 [NoSuchElementException]，而是直接忽略。
     */
    IGNORE_ON_NO_SUCH_TARGET,

    /**
     * 忽略由业务引发的 [DeleteFailureException] 相关异常。
     */
    IGNORE_ON_FAILURE,

    /**
     * 忽略所有由业务引发的异常。
     * 这不会忽略由于不支持而产生的 [UnsupportedOperationException]。
     */
    IGNORE_ON_ANY_FAILURE,

    /**
     * 忽略由于不支持而产生的 [UnsupportedOperationException] 异常。
     */
    IGNORE_ON_UNSUPPORTED;

    public companion object {

        /**
         * 分析 `options` 并得到一个基于 [StandardAnalysis] 的分析结果。
         *
         * @param onEach 在分析每个元素时对它们进行额外的操作。
         */
        @JvmSynthetic
        public inline fun Array<out DeleteOption>.standardAnalysis(onEach: (DeleteOption) -> Unit = {}): StandardAnalysis {
            var value = 0
            forEach { option ->
                if (option is StandardDeleteOption) {
                    value = value or (1 shl option.ordinal)
                }
                onEach(option)
            }

            return StandardAnalysis(value)
        }
    }

    /**
     * 基于 [StandardAnalysis] 的分析结果，以非遍历的方式检测存在的 [StandardDeleteOption] 选项。
     */
    @JvmInline
    public value class StandardAnalysis @PublishedApi internal constructor(private val value: Int) {
        /**
         * 判断是否存在某个标准选项。
         */
        public operator fun contains(option: StandardDeleteOption): Boolean = value and (1 shl option.ordinal) != 0
    }
}


/**
 * @see DeleteSupport.delete
 */
public open class DeleteFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

package love.forte.simbot.quantcat.common.binder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.quantcat.common.listener.FunctionalEventListener
import java.lang.reflect.Method
import java.net.BindException
import kotlin.reflect.*
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.jvm.javaMethod


/**
 * 可以进行动态参数绑定的 [FunctionalEventListener],
 * 可以通过 [binders] 对 [caller] 进行参数绑定。
 *
 * 在 [invoke] 中，如果 [caller] 是可挂起的（`isSuspend = true`）, 则通过可挂起执行。
 * 如果不是可挂起的，则会通过 [runInterruptible] 在可中断中普通执行。
 * [runInterruptible] 默认情况下会使用 [Dispatchers.IO] 作为默认调度器。
 *
 */
public abstract class FunctionalBindableEventListener<R>(
    /**
     * 当前监听函数所对应的执行器。
     */
    public final override val caller: KFunction<R>,
) : FunctionalEventListener<R>() {

    /**
     * binder数组，其索引下标应当与 [KCallable.parameters] 的 [KParameter.index] 相对应。
     * 在使用 [binders] 时，会直接按照其顺序转化为对应的值。
     */
    protected abstract val binders: Array<ParameterBinder>

    /**
     * 对结果的数据类型进行转化。
     */
    protected open fun convertValue(value: Any?, parameter: KParameter): Any? {
        return value
    }

    /**
     * 对 [caller] 执行后的返回值进行处理并转化为 [EventResult]. 可覆盖并自定义结果逻辑。
     *
     * - 如果结果是 [EventResult] 类型，则会直接返回.
     * - 如果返回值为 `null`, 则会返回 [EventResult.invalid].
     *
     * 否则通过 [EventResult.of] 转化为 [EventResult].
     *
     * 当 [result] 为 [Unit] 时，将其视为 `null`。
     *
     */
    protected open fun resultProcess(result: R): EventResult {
        return when (result) {
            is EventResult -> result
            null, Unit -> EventResult.invalid
            else -> EventResult.of(result)
        }
    }

    private val isOptional = caller.parameters.any { it.isOptional }
    private val initialSize = if (isOptional) 0 else caller.parameters.size.initialSize

    /**
     * 可能存在的整合到 [handle] 中的逻辑匹配。
     */
    protected abstract suspend fun match(context: EventListenerContext): Boolean

    /**
     * 函数执行。
     */
    override suspend fun handle(context: EventListenerContext): EventResult {
        if (!match(context)) {
            return EventResult.invalid
        }

        val parameters = caller.parameters
        return if (isOptional) {
            invokeCallBy(context, parameters)
        } else {
            invokeCall(context, parameters)
        }
    }

    private suspend fun invokeCall(context: EventListenerContext, parameters: List<KParameter>): EventResult {
        val binderParameters = binders.mapIndexed { i, b ->
            b.arg(context).getOrElse { e ->
                if (e is BindException) throw e
                else throw BindException(e)
            }.let { value -> convertValue(value, parameters[i]) }
        }

        val args = binderParameters.toTypedArray()

        val result =
            if (caller.isSuspend) caller.callSuspend(args = args)
            else runInterruptible(Dispatchers.IO) { caller.call(args = args) }

        return resultProcess(result)
    }

    private suspend fun invokeCallBy(
        context: EventListenerContext,
        parameters: List<KParameter>,
    ): EventResult {
        val args = LinkedHashMap<KParameter, Any?>(initialSize)
        binders.forEachIndexed { i, b ->
            val value = b.arg(context).getOrElse { e ->
                if (e is BindException) throw e
                else throw BindException(e)
            }
            if (value != ParameterBinder.Ignore) {
                val p = parameters[i]
                args[p] = convertValue(value, p)
            }
        }

        val result =
            if (caller.isSuspend) caller.callSuspendBy(args)
            else runInterruptible(Dispatchers.IO) { caller.callBy(args) }

        return resultProcess(result)
    }

    private inline val Int.initialSize: Int
        get() = (this.toFloat() / 0.75F + 1.0F).toInt()
}

/**
 * [ParameterBinder] 的解析工厂，通过提供部分预处理参数来解析得到 [ParameterBinder] 实例。
 */
public interface ParameterBinderFactory {

    /**
     * 工厂优先级.
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    /**
     * 根据 [Context] 提供的各项参数进行解析与预变异，并得到一个最终的 [ParameterBinder] 到对应的parameter中。
     * 如果返回 [ParameterBinderResult.Empty] ，则视为放弃对目标参数的匹配。
     *
     * 返回值最终会被整合，并按照 [ParameterBinderResult.priority] 的顺序作为此binder的执行顺序。
     *
     * 在监听函数被执行时将会通过解析的 [ParameterBinder] 对参数进行注入，
     * 会依次执行对应的binder取第一个执行成功的.
     *
     */
    public fun resolveToBinder(context: Context): ParameterBinderResult


    /**
     * [ParameterBinderFactory] 进行参数处理时的可用参数内容. 由解析注解监听函数的解析器进行提供。
     */
    public interface Context {
        /**
         * 目标监听函数所对应的函数体。
         */
        public val source: KFunction<*>

        /**
         * 当前的处理参数目标。
         */
        public val parameter: KParameter

        /**
         * 获取 [parameter] 中的 [type.classifier][KClassifier], 并尝试将其转化为 [Java Class][Class].
         * 如果 [classifier][KClassifier] 不是 [KClass] 类型或转化失败，则得到null。
         *
         * _Tips: 如果希望Java中将 [Kotlin Class][KClass] 转化为 [Java Class][Class], 或许可以使用 `JvmClassMappingKt`_
         *
         * @see parameter
         * @see KClassifier
         */
        public val parameterType: Class<*>?
            get() = (parameter.type.classifier as? KClass<*>)?.java

        /**
         * 获取 [source] 并尝试将其转化为 [Java Method][Method]. 无法转化的情况下得到null。
         *
         * _Tips: 如果希望Java中将 [Kotlin Function][KFunction] 转化为 [Java Method][Method], 或许可以使用 `ReflectJvmMapping`_
         *
         * @see source
         * @see KFunction.javaMethod
         */
        public val sourceMethod: Method?
            get() = source.javaMethod
    }
}


/**
 * [ParameterBinderFactory] 的容器，允许通过 ID 获取对应Binder。
 */
public interface ParameterBinderFactoryContainer {
    /**
     * 通过ID尝试获取对应 [ParameterBinderFactory] 实例。
     */
    public operator fun get(id: String): ParameterBinderFactory?

    /**
     * 获取所有的全局binder。
     */
    public fun getGlobals(): List<ParameterBinderFactory>

    /**
     * 将一个 [function] 解析为 [ParameterBinderFactory].
     *
     * 此 function必须遵循规则：
     * - 返回值类型必须是 [ParameterBinder] 或 [ParameterBinderResult] 类型。
     * - 参数或则receiver有且只能有一个，且类型必须是 [ParameterBinderFactory.Context]
     */
    public fun resolveFunctionToBinderFactory(beanId: String? = null, function: KFunction<*>): ParameterBinderFactory
}

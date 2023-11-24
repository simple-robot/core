package love.forte.simbot.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import love.forte.simbot.function.ConfigurerFunction
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 事件调度器。
 * [EventDispatcher] 拥有 [EventProcessor] 和 [EventListenerRegistrar] 的职责，
 * 是对事件调度、事件监听器管理的统一单元。
 *
 * @author ForteScarlet
 */
public interface EventDispatcher : EventProcessor, EventListenerRegistrar {
    // TODO ..?
}

/**
 * DSL marker for [EventDispatcherConfiguration]
 */
@Retention(AnnotationRetention.BINARY)
@DslMarker
public annotation class EventDispatcherConfigurationDSL

/**
 * 针对 [EventDispatcher] 的基础配置类信息。
 * 不同的 [EventDispatcher] 可以对 [EventDispatcherConfiguration] 进行扩展。
 *
 * [EventDispatcherConfiguration] 最少也要满足一些所需的配置内容。
 * 最少也要在不支持的情况下给出警告日志或异常。
 *
 */
public interface EventDispatcherConfiguration {

    /**
     * 用于 [EventDispatcher] 中进行事件调度的协程上下文。
     * [coroutineContext] 会作为调度事件时候使用的调度器，
     * 事件在处理的时候会被切换至此上下文中（例如使用其中的调度器等）。
     *
     * 如果 [coroutineContext] 中存在 [Job]，则此 [Job] 会**被剔除**。
     * [EventDispatcher] 的调度结果最终以流 [Flow] 的形式提供，
     * 因此每次调度任务的生命周期由接收者决定，[Job] 不起作用（[Flow] 的调度上下文中也不允许 [Job] 存在）。
     *
     */
    public var coroutineContext: CoroutineContext

    /**
     * 添加一个拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addInterceptor(
        scope: EventInterceptor.Scope,
        interceptor: EventInterceptor,
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?
    )

    /**
     * 添加一个拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addInterceptor(scope: EventInterceptor.Scope, interceptor: EventInterceptor) {
        addInterceptor(scope, interceptor, null)
    }
}


/**
 * 添加一个作用域为 [EventInterceptor.Scope.GLOBAL] 拦截器与它可能存在的配置信息。
 */
@EventDispatcherConfigurationDSL
public fun EventDispatcherConfiguration.addGlobalScopeInterceptor(
    interceptor: EventInterceptor,
    propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?
) {
    addInterceptor(EventInterceptor.Scope.GLOBAL, interceptor, propertiesConsumer)
}

/**
 * 添加一个作用域为 [EventInterceptor.Scope.GLOBAL] 拦截器与它可能存在的配置信息。
 */
@EventDispatcherConfigurationDSL
public fun EventDispatcherConfiguration.addGlobalScopeInterceptor(interceptor: EventInterceptor) {
    addInterceptor(EventInterceptor.Scope.GLOBAL, interceptor)
}

/**
 * 添加一个作用域为 [EventInterceptor.Scope.EACH] 拦截器与它可能存在的配置信息。
 */
@EventDispatcherConfigurationDSL
public fun EventDispatcherConfiguration.addEachScopeInterceptor(
    interceptor: EventInterceptor,
    propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?
) {
    addInterceptor(EventInterceptor.Scope.EACH, interceptor, propertiesConsumer)
}

/**
 * 添加一个作用域为 [EventInterceptor.Scope.EACH] 拦截器与它可能存在的配置信息。
 */
@EventDispatcherConfigurationDSL
public fun EventDispatcherConfiguration.addEachScopeInterceptor(interceptor: EventInterceptor) {
    addInterceptor(EventInterceptor.Scope.EACH, interceptor)
}

/**
 * [EventDispatcherConfiguration] 的基础抽象类，提供 [EventDispatcherConfiguration] 中基本能力的部分实现或抽象。
 *
 * @see EventDispatcherConfiguration
 */
public abstract class AbstractEventDispatcherConfiguration : EventDispatcherConfiguration {
    override var coroutineContext: CoroutineContext = EmptyCoroutineContext

    //region interceptors
    protected open val interceptors: MutableMap<EventInterceptor.Scope, MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>> =
        mutableMapOf()

    /**
     * 添加一个拦截器与它可能存在的配置信息。
     */
    override fun addInterceptor(
        scope: EventInterceptor.Scope,
        interceptor: EventInterceptor,
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?
    ) {
        interceptors.getOrPut(scope) { mutableListOf() }.add(interceptor to propertiesConsumer)
    }
    //endregion

}



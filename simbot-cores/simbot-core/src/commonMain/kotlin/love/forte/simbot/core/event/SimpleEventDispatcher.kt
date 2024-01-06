package love.forte.simbot.core.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.Application
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.core.application.Simple
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.core.event.impl.SimpleEventDispatcherImpl
import love.forte.simbot.event.*

/**
 * 通过 [Simple] 构建 [Application] 时的实际调度器类型。
 *
 * [SimpleEventDispatcher] 实现 [EventDispatcher] 并提供最基础的完整功能实现。
 */
public interface SimpleEventDispatcher : EventDispatcher

/**
 * 构建一个 [SimpleEventDispatcher].
 */
@ExperimentalSimbotAPI
public fun createSimpleEventDispatcherImpl(configurer: ConfigurerFunction<SimpleEventDispatcherConfiguration>): SimpleEventDispatcher {
    val configuration = SimpleEventDispatcherConfigurationImpl()
    configurer.invokeWith(configuration)
    return SimpleEventDispatcherImpl(configuration)
}

/**
 * 构建一个 [SimpleEventDispatcher].
 */
@ExperimentalSimbotAPI
public fun createSimpleEventDispatcherImpl(configuration: SimpleEventDispatcherConfigurationImpl): SimpleEventDispatcher {
    return SimpleEventDispatcherImpl(configuration)
}


/**
 * Represents the type alias SimpleLP which is an abbreviation for SimpleEventListenerRegistrationProperties.
 * This type alias can be used to define variables or parameters that are expected to have the same semantic meaning as
 * SimpleEventListenerRegistrationProperties.
 *
 * @see SimpleEventListenerRegistrationProperties
 */
public typealias SimpleLP = SimpleEventListenerRegistrationProperties

/**
 * Represents the registration properties for a SimpleEventInterceptor.
 *
 * The SimpleIP (SimpleEventInterceptorRegistrationProperties) typealias is introduced
 * to provide a shorthand notation for the SimpleEventInterceptor registration properties.
 */
public typealias SimpleIP = SimpleEventInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件处理器的注册属性的扩展类型。
 */
public interface SimpleEventListenerRegistrationProperties : EventListenerRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件处理拦截器的注册属性的扩展类型。
 */
public interface SimpleEventInterceptorRegistrationProperties : EventInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件调度拦截器的注册属性的扩展类型。
 */
public interface SimpleEventDispatchInterceptorRegistrationProperties : EventDispatchInterceptorRegistrationProperties

/**
 * 通过 [Simple] 构建 [Application] 时对事件调度器的配置类型的扩展。
 */
public interface SimpleEventDispatcherConfiguration : EventDispatcherConfiguration


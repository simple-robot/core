package love.forte.simbot.spring.configuration

import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerRegistrar
import love.forte.simbot.quantcat.annotations.Listener


/**
 * 针对 [EventDispatcher] 的处理器。
 * 默认会加载使用 [SimbotEventListenerRegistrarProcessor] 和所有 [SimbotEventDispatcherPostConfigurer]。
 *
 * @author ForteScarlet
 */
public interface SimbotEventDispatcherProcessor {
    /**
     * 处理 [dispatcher]
     */
    public fun process(dispatcher: EventDispatcher)
}

/**
 * 默认行为下对 [EventDispatcher] 的配置接口。
 *
 * 可注册多个。
 *
 */
public interface SimbotEventDispatcherPostConfigurer {
    /**
     * 配置 [dispatcher]
     */
    public fun configure(dispatcher: EventDispatcher)
}

/**
 * 针对 [EventListenerRegistrar] 的处理器。
 * 默认会：
 * - 加载使用扫描所有 bean 的函数（标记了 [Listener] 的）并转化、
 * - 所有的注册的 [EventListener] 实例
 * - 所有 [SimbotEventListenerRegistrarPostConfigurer]。
 */
public interface SimbotEventListenerRegistrarProcessor {
    /**
     * 处理 [registrar]
     */
    public fun process(registrar: EventListenerRegistrar)
}

/**
 * 默认行为下对 [EventListenerRegistrar] 的配置接口。
 *
 * 可注册多个。
 *
 */
public interface SimbotEventListenerRegistrarPostConfigurer {
    /**
     * 配置 [registrar]
     */
    public fun configure(registrar: EventListenerRegistrar)
}

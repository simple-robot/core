package love.forte.simbot.event

import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.utils.PriorityConstant


/**
 * 事件监听器的注册器。
 * 用于承载注册、管理监听器的职责。
 *
 * @author ForteScarlet
 */
public interface EventListenerRegistrar {
    /**
     * 注册一个 [EventListener] 并附加部分额外属性信息。
     *
     * 不同的 [EventListenerRegistrar] 可能会支持属性更丰富的 [EventListenerRegistrationProperties],
     *
     * @return 注册后的句柄。可用于撤销/消除此次注册
     */
    public fun register(
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
        listener: EventListener,
    ): EventListenerRegistrationHandle

    /**
     * 注册一个默认属性的 [EventListener].
     *
     * @return 注册后的句柄。可用于撤销/消除此次注册
     */
    public fun register(listener: EventListener): EventListenerRegistrationHandle = register(null, listener)
}

/**
 * 为特定事件类型注册事件处理器函数。
 *
 * @param E The type of event to register the listener for.
 * @param propertiesConsumer An optional function that consumes the configuration properties of the listener registration.
 *        The function should have the signature `ConfigurerFunction<EventListenerRegistrationProperties>`.
 * @param defaultResult The default result function that will be invoked if the event is not of type `E`.
 *        The function should have the signature `EventContext.() -> EventResult`.
 *        By default, it returns an invalid result.
 * @param listenerFunction The listener function that will be invoked when the event is fired.
 *        The function should have the signature `suspend EventContext.(E) -> EventResult`,
 *        where `EventContext` represents the context of the event and `E` is the event type.
 *
 * @throws IllegalArgumentException If the listener function is not provided.
 */
public inline fun <reified E : Event> EventListenerRegistrar.listen(
    propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>? = null,
    crossinline defaultResult: EventContext.() -> EventResult = { EventResult.invalid },
    crossinline listenerFunction: suspend EventContext.(E) -> EventResult,
) {
    register(
        propertiesConsumer = propertiesConsumer,
        listener = { context ->
            val event = context.event
            if (event is E) listenerFunction(context, event) else defaultResult(context)
        })
}

/**
 * 注册事件监听器的额外属性。
 *
 * [EventListenerRegistrationProperties] 可由 [EventListenerRegistrar] 的实现者自由扩展，
 * 但应当至少能够支持最基础的几项属性，并至少在不支持的情况下提供警告日志或异常。
 */
public interface EventListenerRegistrationProperties {
    /**
     * 优先级。数值越小优先级越高。通常默认为 [PriorityConstant.NORMAL]。
     */
    public var priority: Int

    /**
     * 为此监听函数添加一个独特的拦截器。
     * 拦截器的优先级与最终此监听函数被添加的全局性拦截器共享。
     */
    public fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    )

    /**
     * 为此监听函数添加一个独特的拦截器。
     */
    public fun addInterceptor(interceptor: EventInterceptor) {
        addInterceptor(null, interceptor)
    }
}

/**
 * 监听器注册成功后得到的对应的句柄。
 *
 */
public interface EventListenerRegistrationHandle {
    /**
     * 取消对应监听器的注册。
     */
    public fun dispose()
}




package love.forte.simbot.event


/**
 * 事件监听函数的注册器。
 * 用于承载注册、管理监听函数的职责。
 *
 * @author ForteScarlet
 */
public interface EventListenerRegistrar {
    /**
     * 注册一个 [EventListener] 并附加部分额外属性信息。
     *
     * 不同的 [EventListenerRegistrar] 可能会支持属性更丰富的 [RegistrationProperties],
     *
     * @return 注册后的句柄。可用于撤销/消除此次注册
     */
    public fun register(listener: EventListener, properties: RegistrationProperties?): EventListenerHandle

    /**
     * 注册一个默认属性的 [EventListener].
     *
     * @return 注册后的句柄。可用于撤销/消除此次注册
     */
    public fun register(listener: EventListener): EventListenerHandle = register(listener, null)
}

/**
 * 配置属性并注册一个 [EventListener]。
 *
 * @see EventListenerRegistrar.register
 */
public inline fun EventListenerRegistrar.register(
    listener: EventListener,
    block: RegistrationPropertiesBuilder.() -> Unit
): EventListenerHandle =
    register(listener, RegistrationPropertiesBuilder().also(block).build())

/**
 * 注册事件监听函数的额外属性。
 *
 * [RegistrationProperties] 可由 [EventListenerRegistrar] 的实现者自由扩展，
 * 但应当至少能够支持最基础的几项属性，并至少在不支持的情况下提供警告日志或异常。
 *
 * 如果不关心可能存在的额外属性，则可以使用 [RegistrationPropertiesBuilder]
 * 快速构建一个基础的 [RegistrationProperties] 实现。
 * 实现者也可以通过 [RegistrationPropertiesBuilder] 扩展一个构建器。
 */
public interface RegistrationProperties {
    /**
     * 优先级。数值越小优先级越高。通常默认为最小 [Int.MIN_VALUE]。
     */
    public val priority: Int
}

/**
 * 用于构建最基础的 [RegistrationProperties] 的构建器。
 *
 */
public open class RegistrationPropertiesBuilder {
    /**
     * 优先级。默认为 [Int.MIN_VALUE]。
     */
    public open var priority: Int = Int.MIN_VALUE

    /**
     * 根据当前配置属性构建一个 [RegistrationProperties]。
     */
    public open fun build(): RegistrationProperties = Impl(priority)


    private data class Impl(override val priority: Int) : RegistrationProperties
}


/**
 * 监听函数注册成功后得到的对应的句柄。
 *
 */
public interface EventListenerHandle {
    /**
     * 取消对应监听函数的注册。
     */
    public fun dispose()
}




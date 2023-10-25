package love.forte.simbot.component

import kotlinx.serialization.modules.SerializersModule

/**
 * 一个 **组件**。
 *
 * 组件是针对某个平台的bot能力实现的标识单位。
 *
 * 在构建 [Application][love.forte.simbot.application.Application] 的过程中，
 * [Component] 是事件调度器之后最先被加载处理的内容。
 *
 * @author ForteScarlet
 */
public interface Component {
    /**
     * 一个组件的ID。
     * 组件id建议使用类似于Java包路径的格式，
     * 例如 `org.example.Sample` 并尽量避免重复。
     */
    public val id: String

    /**
     * 组件对外提供的统合所有所需的序列化信息。
     * 通常为 message 类型的序列化或文件配置类的序列化信息。
     */
    public val serializersModule: SerializersModule
}

/**
 * [Component] 的工厂函数，用于配置并预构建 [Component] 实例。
 *
 * @see Component
 * @param Com 目标组件类型
 * @param Conf 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface ComponentFactory<Com : Component, Conf : Any> {
    /**
     * 用于 [ComponentFactory] 在内部整合时的标识类型。
     */
    public interface Key

    /**
     * 工厂函数的标识。
     * [key] 应当是一个针对当前类型的 [ComponentFactory] 的 **常量** 实例。
     */
    public val key: Key

    /**
     * 提供配置逻辑函数，并得到组件的结果。
     *
     * @param configurer 配置类的配置逻辑。
     */
    public fun create(configurer: ComponentFactoryConfigurer<Conf>): Com

    /**
     * 使用默认的配置（没有额外配置逻辑）构建并得到组件的结果。
     */
    public fun create(): Com = create {}
}


/**
 * 组件工厂的配置逻辑函数。
 * @see ComponentFactory.create
 */
public fun interface ComponentFactoryConfigurer<in Conf> {
    /**
     * 配置逻辑。
     */
    public operator fun Conf.invoke()
}

// region Exceptions
/**
 * Component exception.
 */
public open class ComponentException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 没有符合条件的 Component 时
 */
public class NoSuchComponentException : ComponentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * Component 已经存在时
 */
public class ComponentAlreadyExistsException : ComponentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
// endregion

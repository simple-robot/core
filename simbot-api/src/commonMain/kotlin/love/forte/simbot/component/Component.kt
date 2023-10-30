package love.forte.simbot.component

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.utils.MergeableFactoriesConfigurator
import love.forte.simbot.utils.MergeableFactory

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
 * @param COM 目标组件类型
 * @param CONF 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface ComponentFactory<COM : Component, CONF : Any> : MergeableFactory<ComponentFactory.Key, COM, CONF> {
    /**
     * 用于 [ComponentFactory] 在内部整合时的标识类型。
     *
     * 更多说明参阅 [MergeableFactory.Key]。
     *
     * @see ComponentFactory.key
     * @see MergeableFactory.key
     */
    public interface Key : MergeableFactory.Key
}

/**
 * 提供给 [ComponentFactoriesConfigurator] 用于配置 [Component] 的上下文信息。
 * 可以得到来自 [Application][love.forte.simbot.application.Application] 的初始化配置信息。
 */
public interface ComponentConfigureContext {
    // TODO application configurations

}


/**
 * 用于对 [ComponentFactory] 进行聚合组装的配置器。
 */
public class ComponentFactoriesConfigurator(
    configurators: Map<ComponentFactory.Key, Configurator<Any, ComponentConfigureContext>> = emptyMap(),
    factories: Map<ComponentFactory.Key, (ComponentConfigureContext) -> Component> = emptyMap(),
) : MergeableFactoriesConfigurator<ComponentConfigureContext, Component, ComponentFactory.Key>(configurators, factories)


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

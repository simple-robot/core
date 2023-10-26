package love.forte.simbot.component

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.component.ComponentFactoriesConfigurator.Configurator

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
     *
     * [Key] 的实现应用于 [ComponentFactory.key]。
     * [Key] 会被作为一个用于区分 [ComponentFactory] 的 `key` 使用，
     * 并可能会应用于诸如 HashMap 的键上。
     *
     * 因此，在 Kotlin 中，[Key] 的实现推荐为一个 `object` 类型
     * （例如 [ComponentFactory] 实现对应的伴生对象）。
     * 在 JVM 或其他实现中，[Key] 的实现至少应保证其实例唯一，
     * 或 [hashCode] 与 [equals] 直接具有正常的关联性。
     *
     * @see ComponentFactory.key
     *
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


/**
 * 用于对 [ComponentFactory] 进行聚合组装的配置器。
 */
public class ComponentFactoriesConfigurator<CONTEXT>(
    configurators: Map<ComponentFactory.Key, Configurator<Any, CONTEXT>> = emptyMap(),
    factories: Map<ComponentFactory.Key, (CONTEXT) -> Component> = emptyMap(),
) {
    private val configurators = configurators.toMutableMap()
    private val factories = factories.toMutableMap()

    /**
     * Configurer fun type for [ComponentFactoriesConfigurator.add].
     */
    public fun interface Configurator<in Conf, in Context> {
        /**
         * invoker.
         */
        public operator fun Conf.invoke(context: Context)
    }

    // TODO

    /**
     *
     *
     */
    public fun <Com : Component, Conf : Any> add(
        factory: ComponentFactory<Com, Conf>,
        configurator: Configurator<Conf, CONTEXT>
    ) {
        val key = factory.key
        val newConfig = newConfigurator(key, configurators, configurator)
        configurators[key] = newConfig

        if (key in factories) return

        factories[key] = {
            val configurator0 = configurators[key]!!
            //
//            factory.create(configurator0)
            TODO()
        }
    }


    private fun <CONFIG : Any> newConfigurator(
        key: ComponentFactory.Key,
        configurations: Map<ComponentFactory.Key, Configurator<Any, CONTEXT>>,
        configurator: Configurator<CONFIG, CONTEXT>
    ): (Configurator<Any, CONTEXT>) {
        val oldConfig = configurations[key]

        @Suppress("UNCHECKED_CAST")
        return if (oldConfig != null) {
            Configurator { context ->
                this as CONFIG
                oldConfig.apply { invoke(context) }
                configurator.apply { invoke(context) }
            }
        } else {
            Configurator { context ->
                this as CONFIG
                configurator.apply { invoke(context) }
            }
        }
    }
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

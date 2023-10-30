package love.forte.simbot.application

import love.forte.simbot.utils.MergeableFactoriesConfigurator
import love.forte.simbot.utils.MergeableFactory

/**
 *
 * 一个 **插件**。
 *
 * [Plugin] 应用于 [Application] 中，
 * 在所有组件 [Component][love.forte.simbot.component.Component]
 * 加载完成后进入配置阶段。
 *
 * @author ForteScarlet
 */
public interface Plugin {

}

/**
 * [Plugin] 的工厂函数，用于配置并预构建 [Plugin] 实例。
 *
 * @see Plugin
 * @param P 目标组件类型
 * @param CONF 配置类型。配置类型应是一个可变类，以便于在 DSL 中进行动态配置。
 */
public interface PluginFactory<P : Plugin, CONF : Any> : MergeableFactory<PluginFactory.Key, P, CONF> {
    /**
     * 用于 [PluginFactory] 在内部整合时的标识类型。
     *
     * 更多说明参阅 [MergeableFactory.Key]。
     *
     * @see PluginFactory.key
     * @see MergeableFactory.key
     */
    public interface Key : MergeableFactory.Key
}


/**
 * 用于对 [PluginFactory] 进行聚合组装的配置器。
 */
public class PluginFactoriesConfigurator<CONTEXT>(
    configurators: Map<PluginFactory.Key, Configurator<Any, CONTEXT>> = emptyMap(),
    factories: Map<PluginFactory.Key, (CONTEXT) -> Plugin> = emptyMap(),
) : MergeableFactoriesConfigurator<CONTEXT, Plugin, PluginFactory.Key>(configurators, factories)


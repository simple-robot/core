@file:JvmName("PluginFactoryProviders")

package love.forte.simbot.plugin

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

/**
 * 用于支持自动加载 [PluginFactory] 的 SPI 接口。
 *
 * @author ForteScarlet
 */
public interface PluginFactoryProvider<CONF : Any> {
    /**
     * 得到提供的 [PluginFactory] 实例。
     */
    public fun provide(): PluginFactory<*, CONF>

    /**
     * 提供额外配置类的类型用于一些可自动加载的加载器。
     * 如果返回 `null` 则代表不提供、不加载可自动加载的额外配置类型。
     *
     * 这些加载器的类型建议由 [PluginFactory] 的实现者提供，
     * 因此 [ServiceLoader] 也需要由实现者直接提供。
     */
    public fun configurersLoader(): ServiceLoader<out PluginFactoryConfigurerProvider<CONF>>?
}


private class ProviderPluginFactory<P : Plugin, CONF : Any>(
    private val factory: PluginFactory<P, CONF>,
    private val configurers: List<PluginFactoryConfigurerProvider<CONF>>
) : PluginFactory<P, CONF> {
    override val key: PluginFactory.Key
        get() = factory.key

    override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): P {
        return factory.create(context) {
            configurer.invokeWith(this)
            configurers.forEach {
                it.configure(this)
            }
        }
    }
}


/**
 * 用于在加载 [PluginFactoryProvider] 后、构建对应的 [Plugin] 时，
 * 作为自动加载的额外配置类型的 SPI，
 * 在使用 [loadPluginFactoriesFromProviders] （或其他衍生函数）
 * 且参数 `loadConfigurers` 为 `true` 时会被自动加载并作为构建 [Plugin] 的前置配置逻辑。
 *
 * @author ForteScarlet
 */
public interface PluginFactoryConfigurerProvider<CONF : Any> {
    /**
     * 处理配置
     */
    public fun configure(config: CONF)
}


/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginProviders(loader: ClassLoader): Stream<PluginFactoryProvider<*>> {
    return ServiceLoader.load(PluginFactoryProvider::class.java, loader)
        .stream().map { it.get() }
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginProviders(): Stream<PluginFactoryProvider<*>> {
    return ServiceLoader.load(PluginFactoryProvider::class.java)
        .stream().map { it.get() }
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginFactoriesFromProviders(
    loader: ClassLoader,
    loadConfigurers: Boolean
): Stream<PluginFactory<*, *>> {
    return loadPluginProviders(loader).map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [ServiceLoader] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginFactoriesFromProviders(loadConfigurers: Boolean): Stream<PluginFactory<*, *>> {
    return loadPluginProviders().map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [loadPluginFactoriesFromProviders] 加载并安装所有可寻得的组件。
 *
 * @param loadConfigurers 是否同时加载所有可用的前置配置
 */
public fun PluginInstaller.findAnyInstallAllPlugins(loadConfigurers: Boolean) {
    loadPluginFactoriesFromProviders(loadConfigurers).forEach { factory ->
        install(factory)
    }
}

private fun <C : Any> PluginFactoryProvider<C>.loadConfigurersAndToPlugin(
    loadConfigurers: Boolean
): PluginFactory<*, C> {
    val factory = provide()
    if (!loadConfigurers) {
        return factory
    }

    val loader = configurersLoader() ?: return factory
    val configurerList = loader.stream().map { it.get() }.toList()

    return ProviderPluginFactory(factory, configurerList)
}

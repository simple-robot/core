package love.forte.simbot.application

import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import love.forte.simbot.plugin.PluginFactory


/**
 * 用于构建一个 [ApplicationFactory] 的工厂。
 *
 * [ApplicationFactory] 通常应以常量形式实现，例如一个 Kotlin object。
 *
 * @see Application
 *
 * @author ForteScarlet
 */
public interface ApplicationFactory<out A : Application, C : ApplicationConfigurationBuilder, L : ApplicationLauncher<A>> {

    /**
     * 通过配置逻辑 [configurer] 构建一个预处理启动器 [ApplicationLauncher]。
     *
     * ```kotlin
     * create {
     *    config {
     *      ...
     *    }
     *    install(...) {
     *      ...
     *    }
     * }
     * ```
     *
     */
    public fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C>>): L
}


// TODO ApplicationFactoryConfigurer

/**
 *
 * @see AbstractApplicationFactoryConfigurer
 */
public interface ApplicationFactoryConfigurer<C : ApplicationConfigurationBuilder> {

    public fun config(configurer: ConfigurerFunction<C>)

    public fun <COM : Component, CONF : Any> install(
        componentFactory: ComponentFactory<COM, CONF>,
        configurer: ConfigurerFunction<CONF>
    )

    public fun <COM : Component, CONF : Any> install(componentFactory: ComponentFactory<COM, CONF>) {
        install(componentFactory) {}
    }

    public fun <P : Plugin, CONF : Any> install(
        pluginFactory: PluginFactory<P, CONF>,
        configurer: ConfigurerFunction<CONF>
    )

    public fun <P : Plugin, CONF : Any> install(pluginFactory: PluginFactory<P, CONF>) {
        install(pluginFactory) {}
    }

}


// TODO
/*
    ApplicationFactory.create {
        config {
            ...
        }

        install() {
            // ...
        }
    }

 */

public abstract class AbstractApplicationFactoryConfigurer<C : ApplicationConfigurationBuilder>(
    protected open val configConfigurers: MutableList<ConfigurerFunction<C>> = mutableListOf(),
    protected open val componentFactoriesConfigurator: ComponentFactoriesConfigurator = ComponentFactoriesConfigurator(),
    protected open val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : ApplicationFactoryConfigurer<C> {
    override fun config(configurer: ConfigurerFunction<C>) {
        configConfigurers.add(configurer)
    }

    override fun <COM : Component, CONF : Any> install(
        componentFactory: ComponentFactory<COM, CONF>,
        configurer: ConfigurerFunction<CONF>
    ) {
        componentFactoriesConfigurator.add(componentFactory, configurer)
    }

    override fun <P : Plugin, CONF : Any> install(
        pluginFactory: PluginFactory<P, CONF>,
        configurer: ConfigurerFunction<CONF>
    ) {
        pluginFactoriesConfigurator.add(pluginFactory, configurer)
    }

    protected open fun createConfig(configBuilder: C): ApplicationConfiguration {
        configConfigurers.forEach { it.invokeWith(configBuilder) }
        return configBuilder.build()
    }

    protected open fun createAllComponents(context: ComponentConfigureContext): List<Component> {
        return componentFactoriesConfigurator.createAll(context)
    }

    protected open fun createAllPlugins(context: PluginConfigureContext): List<Plugin> {
        return pluginFactoriesConfigurator.createAll(context)
    }
}

/**
 * [Application] 的预处理启动器。
 * 当执行 [ApplicationLauncher.launch] 时会构建并启动 [Application]。
 */
public interface ApplicationLauncher<out A : Application> {

    public suspend fun launch(): A

}

/**
 * 构建一个内部的 [ApplicationLauncher] 默认实现并简单包装一个启动逻辑 [block]。
 */
public inline fun <A : Application> applicationLauncher(crossinline block: suspend () -> A): ApplicationLauncher<A> =
    object : ApplicationLauncher<A> {
        override suspend fun launch(): A = block()
    }

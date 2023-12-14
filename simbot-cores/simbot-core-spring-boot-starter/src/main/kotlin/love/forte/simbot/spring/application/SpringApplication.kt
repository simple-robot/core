package love.forte.simbot.spring.application

import love.forte.simbot.application.*
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeBy
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import love.forte.simbot.spring.application.internal.SpringApplicationImpl
import org.springframework.context.ApplicationContext
import kotlin.coroutines.CoroutineContext


public interface SpringApplication : Application

public object Spring :
    ApplicationFactory<SpringApplication, SpringApplicationBuilder, SpringApplicationLauncher, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration> {
    override fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>>?): SpringApplicationLauncher {
        return SpringApplicationLauncherImpl { create0(configurer) }
    }

    private fun create0(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>>?): SpringApplicationImpl {
        val springConfigurer = SpringApplicationFactoryConfigurer().invokeBy(configurer)


        TODO()
    }
}


public class SpringApplicationBuilder : AbstractApplicationBuilder() {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    public var applicationConfigurationProperties: SpringApplicationConfigurationProperties =
        SpringApplicationConfigurationProperties()

    /**
     * Spring [ApplicationContext].
     */
    public lateinit var applicationContext: ApplicationContext


    internal fun build(): SpringApplicationConfiguration =
        SpringApplicationConfigurationImpl(
            coroutineContext,
            applicationConfigurationProperties,
            applicationContext
        )

}

public interface SpringApplicationConfiguration : ApplicationConfiguration {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    public val applicationConfigurationProperties: SpringApplicationConfigurationProperties

    /**
     * Spring [ApplicationContext].
     */
    public val applicationContext: ApplicationContext
}

public interface SpringApplicationLauncher : ApplicationLauncher<SpringApplication>

public interface SpringApplicationEventRegistrar : ApplicationEventRegistrar

public interface SpringEventDispatcherConfiguration : EventDispatcherConfiguration

////

private class SpringApplicationLauncherImpl(
    private val applicationCreator: () -> SpringApplicationImpl
) : SpringApplicationLauncher {
    override suspend fun launch(): SpringApplication {
        val application = applicationCreator()

        application.events.invokeOnEach(ApplicationLaunchStage.Launch) {
            invoke(application)
        }

        return application
    }
}


private class SpringApplicationFactoryConfigurer(
    public override val configConfigurers: MutableList<ConfigurerFunction<SpringApplicationBuilder>> = mutableListOf(),
    public override val applicationEventRegistrarConfigurations: MutableList<ConfigurerFunction<SpringApplicationEventRegistrar>> = mutableListOf(),
    public override val eventDispatcherConfigurers: MutableList<ConfigurerFunction<SpringEventDispatcherConfiguration>> = mutableListOf(),
    public override val componentFactoriesConfigurator: ComponentFactoriesConfigurator = ComponentFactoriesConfigurator(),
    public override val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : AbstractApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>(
    configConfigurers,
    applicationEventRegistrarConfigurations,
    eventDispatcherConfigurers,
    componentFactoriesConfigurator,
    pluginFactoriesConfigurator
) {
    // TODO?
    fun createConfigInternal(configBuilder: SpringApplicationBuilder): SpringApplicationConfiguration {
        return createConfig(configBuilder) {
            it.build()
        }
    }

    public override fun createAllComponents(context: ComponentConfigureContext): List<Component> {
        return super.createAllComponents(context)
    }

    public override fun createAllPlugins(context: PluginConfigureContext): List<Plugin> {
        return super.createAllPlugins(context)
    }
}


private class SpringApplicationConfigurationImpl(
    override val coroutineContext: CoroutineContext,
    override val applicationConfigurationProperties: SpringApplicationConfigurationProperties,
    override val applicationContext: ApplicationContext
) : SpringApplicationConfiguration

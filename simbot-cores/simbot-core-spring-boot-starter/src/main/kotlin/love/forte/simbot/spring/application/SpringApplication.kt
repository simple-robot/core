package love.forte.simbot.spring.application

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.*
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration
import love.forte.simbot.core.event.createSimpleEventDispatcherImpl
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import love.forte.simbot.spring.application.internal.SpringApplicationImpl
import love.forte.simbot.spring.application.internal.SpringEventDispatcherConfigurationImpl
import org.springframework.context.ApplicationContext
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * 使用在 Spring Starter 中的 [Application] 实现。
 * 主要由内部使用。
 */
public interface SpringApplication : Application

/**
 * Factory for [SpringApplication].
 */
public object Spring :
    ApplicationFactory<SpringApplication, SpringApplicationBuilder, SpringApplicationLauncher, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration> {
    override fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>>?): SpringApplicationLauncher {
        return SpringApplicationLauncherImpl { create0(configurer) }
    }

    @OptIn(ExperimentalSimbotAPI::class)
    private fun create0(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>>?): SpringApplicationImpl {
        val springConfigurer = SpringApplicationFactoryConfigurer().invokeBy(configurer)
        val configuration = springConfigurer.createConfigInternal(SpringApplicationBuilder())

        // register?


        // 事件调度器
        val dispatcherConfiguration = SpringEventDispatcherConfigurationImpl(SimpleEventDispatcherConfigurationImpl())
        springConfigurer.eventDispatcherConfigurers.forEach(dispatcherConfiguration::invokeBy)

        // 合并 Application coroutineContext into dispatcher coroutineContext, 且不要Job
        val minJobDispatcherContext = dispatcherConfiguration.coroutineContext.minusKey(Job)
        val minJobApplicationContext = configuration.coroutineContext.minusKey(Job)
        dispatcherConfiguration.coroutineContext = minJobApplicationContext + minJobDispatcherContext


        val dispatcher = createSimpleEventDispatcherImpl(dispatcherConfiguration.simple)

        // 事件注册器

        // components

        // plugins


        TODO()
    }
}

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationBuilder]
 *
 */
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

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationLauncher] 实现。
 *
 */
public interface SpringApplicationLauncher : ApplicationLauncher<SpringApplication>

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationEventRegistrar] 实现。
 *
 */
public interface SpringApplicationEventRegistrar : ApplicationEventRegistrar

/**
 * Simbot Spring Boot Starter 中使用的调度器配置。
 *
 */
public interface SpringEventDispatcherConfiguration : SimpleEventDispatcherConfiguration {

    /**
     * 添加一个 [Executor] 并作为 [协程调度器][CoroutineDispatcher] 添加到 [CoroutineContext] 中。
     */
    public fun setExecutorDispatcher(executor: Executor) {
        coroutineContext += executor.asCoroutineDispatcher()
    }


}

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

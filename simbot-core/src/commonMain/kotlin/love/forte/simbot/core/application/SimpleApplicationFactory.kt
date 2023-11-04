package love.forte.simbot.core.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.application.*
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.core.event.SimpleEventDispatcher
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration
import love.forte.simbot.core.event.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.core.event.SimpleEventDispatcherImpl
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeBy
import love.forte.simbot.function.invokeWith
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactoriesConfigurator
import kotlin.coroutines.CoroutineContext


/**
 *
 * @see Simple
 *
 */
private class SimpleApplicationImpl(
    override val configuration: SimpleApplicationConfiguration,
    override val eventDispatcher: SimpleEventDispatcher,
    override val components: Components,
    override val plugins: Plugins,
    internal val events: ApplicationLaunchStages
) : SimpleApplication {
    private val job: Job
    override val coroutineContext: CoroutineContext

    init {
        val newJob = SupervisorJob(configuration.coroutineContext[Job])
        val newCoroutineContext = configuration.coroutineContext.minusKey(Job) + newJob

        this.job = newJob
        this.coroutineContext = newCoroutineContext
    }

    private inline fun <C : Any, reified H : NormalApplicationEventHandler<C>> invokeNormalHandler(
        stage: ApplicationLaunchStage<H>, block: H.() -> Unit
    ) {
        events[stage]?.forEach { handler ->
            (handler as? H)?.also { handler0 ->
                block(handler0)
            }
        }
    }

    override fun cancel() {
        invokeNormalHandler(ApplicationLaunchStage.RequestCancel) {
            invoke(this@SimpleApplicationImpl)
        }

        job.cancel()

        invokeNormalHandler(ApplicationLaunchStage.Cancelled) {
            invoke(this@SimpleApplicationImpl)
        }
    }

    override suspend fun join() {
        job.join()
    }
}

/**
 * 用于构建 [SimpleApplication] 实例的 [ApplicationFactory] 实现。
 *
 */
public object Simple :
    ApplicationFactory<SimpleApplication, SimpleAbstractApplicationBuilder, SimpleApplicationLauncher, ApplicationEventRegistrar, SimpleEventDispatcherConfiguration> {

    override fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SimpleAbstractApplicationBuilder, ApplicationEventRegistrar, SimpleEventDispatcherConfiguration>>): SimpleApplicationLauncher {
        return SimpleApplicationLauncherImpl { create0(configurer) }
    }

    private fun create0(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SimpleAbstractApplicationBuilder, ApplicationEventRegistrar, SimpleEventDispatcherConfiguration>>): SimpleApplicationImpl {
        val simpleConfigurer = SimpleApplicationFactoryConfigurer().invokeBy(configurer)

        // 配置信息
        val configuration = simpleConfigurer.createConfigInternal(SimpleAbstractApplicationBuilder())

        val registrar = object : AbstractApplicationEventRegistrar() {
            public override val events: MutableMap<ApplicationLaunchStage<*>, MutableList<ApplicationEventHandler>>
                get() = super.events
        }

        // 事件调度器
        val dispatcherConfiguration = SimpleEventDispatcherConfigurationImpl()
        simpleConfigurer.eventDispatcherConfigurers.forEach { cf ->
            dispatcherConfiguration.invokeBy(cf)
        }

        // TODO 合并 Application coroutineContext into dispatcher coroutineContext

        val eventDispatcher: SimpleEventDispatcher = SimpleEventDispatcherImpl(dispatcherConfiguration)

        // 事件注册器
        simpleConfigurer.applicationEventRegistrarConfigurations.forEach { c ->
            c.invokeWith(registrar)
        }

        // components
        val components = simpleConfigurer.componentFactoriesConfigurator.createAll(object : ComponentConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = configuration

            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = registrar
        }).toComponents()

        // plugins
        val plugins = simpleConfigurer.pluginFactoriesConfigurator.createAll(object : PluginConfigureContext {
            override val applicationConfiguration: ApplicationConfiguration
                get() = configuration

            override val applicationEventRegistrar: ApplicationEventRegistrar
                get() = registrar

            override val components: Components
                get() = components

            override val eventDispatcher: EventDispatcher
                get() = eventDispatcher
        }).toPlugins()

        val events = applicationLaunchStages(registrar.events.mapValues { it.value.toList() })

        return SimpleApplicationImpl(
            configuration,
            eventDispatcher,
            components,
            plugins,
            events
        )
    }
}

private class SimpleApplicationFactoryConfigurer(
    public override val configConfigurers: MutableList<ConfigurerFunction<SimpleAbstractApplicationBuilder>> = mutableListOf(),
    public override val applicationEventRegistrarConfigurations: MutableList<ConfigurerFunction<ApplicationEventRegistrar>> = mutableListOf(),
    public override val eventDispatcherConfigurers: MutableList<ConfigurerFunction<SimpleEventDispatcherConfiguration>> = mutableListOf(),
    public override val componentFactoriesConfigurator: ComponentFactoriesConfigurator = ComponentFactoriesConfigurator(),
    public override val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : AbstractApplicationFactoryConfigurer<SimpleAbstractApplicationBuilder, ApplicationEventRegistrar, SimpleEventDispatcherConfiguration>(
    configConfigurers,
    applicationEventRegistrarConfigurations,
    eventDispatcherConfigurers,
    componentFactoriesConfigurator,
    pluginFactoriesConfigurator
) {
    fun createConfigInternal(configBuilder: SimpleAbstractApplicationBuilder): SimpleApplicationConfiguration {
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


public class SimpleAbstractApplicationBuilder : AbstractApplicationBuilder() {
    internal fun build(): SimpleApplicationConfiguration = SimpleApplicationConfigurationImpl(coroutineContext)
}

private class SimpleApplicationConfigurationImpl(override val coroutineContext: CoroutineContext) :
    SimpleApplicationConfiguration

private class SimpleApplicationLauncherImpl(
    private val applicationCreator: () -> SimpleApplicationImpl
) : SimpleApplicationLauncher {
    override suspend fun launch(): SimpleApplicationImpl {
        val application = applicationCreator()

        application.events.invokeOnEach(ApplicationLaunchStage.Launch) {
            invoke(application)
        }

        return application
    }
}

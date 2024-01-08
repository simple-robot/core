package love.forte.simbot.spring

import love.forte.simbot.spring.configuration.*
import love.forte.simbot.spring.configuration.application.*
import love.forte.simbot.spring.configuration.binder.DefaultBinderManagerProvidersConfiguration
import love.forte.simbot.spring.configuration.binder.ResolveBinderManagerProcessor
import love.forte.simbot.spring.configuration.config.DefaultSimbotApplicationConfigurationProcessorConfiguration
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerFunctionProcessor
import org.springframework.context.annotation.Import


@Target(AnnotationTarget.CLASS)
@Import(
    SimbotSpringPropertiesConfiguration::class,
    // defaults
    DefaultSimbotApplicationConfigurationProcessorConfiguration::class,
    DefaultSimbotDispatcherProcessorConfiguration::class,
    DefaultSimbotComponentInstallProcessorConfiguration::class,
    DefaultSimbotPluginInstallProcessorConfiguration::class,
    DefaultSimbotSpringApplicationLauncherFactoryConfiguration::class,
    DefaultSimbotEventDispatcherProcessorConfiguration::class,
    DefaultBinderManagerProvidersConfiguration::class,
    // listeners directly
    DefaultSimbotEventListenerRegistrarProcessorConfiguration::class,
    // launcher factory & launcher
    DefaultSimbotSpringApplicationLauncherFactoryConfiguration::class,
    DefaultSimbotApplicationLauncherFactoryProcessorConfiguration::class,
    SimbotApplicationConfiguration::class,
    // app
    DefaultSimbotSpringApplicationProcessorConfiguration::class,
    // binders
    ResolveBinderManagerProcessor::class,
    // post listeners
    SimbotEventListenerFunctionProcessor::class,
    SimbotApplicationRunner::class

)
public annotation class EnableSimbot

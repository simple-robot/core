package love.forte.simbot.spring

import love.forte.simbot.spring.configuration.*
import org.springframework.context.annotation.Import


@Target(AnnotationTarget.CLASS)
@Import(
    SimbotSpringPropertiesConfiguration::class,
    DefaultSimbotDispatcherProcessorConfiguration::class,
    DefaultSimbotComponentInstallProcessorConfiguration::class,
    DefaultSimbotPluginInstallProcessorConfiguration::class,
    DefaultSimbotSpringApplicationLauncherProcessorConfiguration::class,
    DefaultSimbotSpringApplicationProcessorConfiguration::class,
    DefaultSimbotEventListenerRegistrarProcessorConfiguration::class,
    DefaultSimbotEventDispatcherProcessorConfiguration::class,
)
public annotation class EnableSimbot

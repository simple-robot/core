package love.forte.simbot.spring.configuration.application

import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.spring.application.*
import love.forte.simbot.spring.configuration.SimbotComponentInstallProcessor
import love.forte.simbot.spring.configuration.SimbotDispatcherProcessor
import love.forte.simbot.spring.configuration.SimbotPluginInstallProcessor
import love.forte.simbot.spring.configuration.config.SimbotApplicationConfigurationProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * [SimbotApplicationLauncherFactory] 的默认逻辑实现
 * [DefaultSimbotApplicationLauncherFactory] 的配置类。
 *
 */
@Configuration(proxyBeanMethods = true)
public open class DefaultSimbotSpringApplicationLauncherFactoryConfiguration {
    @Bean(DEFAULT_SPRING_APPLICATION_LAUNCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotApplicationLauncherFactory::class)
    public open fun defaultSpringApplicationLauncherProcessor(
        @Autowired(required = false) builderPreConfigurers: List<SimbotApplicationLauncherPreConfigurer>? = null,
        @Autowired(required = false) builderPostConfigurers: List<SimbotApplicationLauncherPostConfigurer>? = null,
        @Autowired applicationConfigurationProcessor: SimbotApplicationConfigurationProcessor,
        @Autowired dispatcherProcessor: SimbotDispatcherProcessor,
        @Autowired componentProcessor: SimbotComponentInstallProcessor,
        @Autowired pluginProcessor: SimbotPluginInstallProcessor,
    ): DefaultSimbotApplicationLauncherFactory =
        DefaultSimbotApplicationLauncherFactory(
            builderPreConfigurers ?: emptyList(),
            builderPostConfigurers ?: emptyList(),
            applicationConfigurationProcessor,
            dispatcherProcessor,
            componentProcessor,
            pluginProcessor,
        )

    public companion object {
        public const val DEFAULT_SPRING_APPLICATION_LAUNCHER_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSpringApplicationLauncherProcessor"
    }
}


/**
 * [SimbotApplicationLauncherFactory] 的默认实现。
 *
 */
public class DefaultSimbotApplicationLauncherFactory(
    private val builderPreConfigurers: List<SimbotApplicationLauncherPreConfigurer>,
    private val builderPostConfigurers: List<SimbotApplicationLauncherPostConfigurer>,
    private val applicationConfigurationProcessor: SimbotApplicationConfigurationProcessor,
    private val dispatcherProcessor: SimbotDispatcherProcessor,
    private val componentProcessor: SimbotComponentInstallProcessor,
    private val pluginProcessor: SimbotPluginInstallProcessor,
) : SimbotApplicationLauncherFactory {
    override fun process(factory: Spring): SpringApplicationLauncher {
        return factory.create {
            // pre
            builderPreConfigurers.forEach { it.configure(this) }

            configure0()

            // post
            builderPostConfigurers.forEach { it.configure(this) }
        }
    }

    private fun ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>.configure0() {
        config {
            applicationConfigurationProcessor.process(this)
        }
        // dispatchers
        eventDispatcher {
            dispatcherProcessor.process(this)
        }
        // components
        componentProcessor.process(this)
        // plugins
        pluginProcessor.process(this)
    }
}

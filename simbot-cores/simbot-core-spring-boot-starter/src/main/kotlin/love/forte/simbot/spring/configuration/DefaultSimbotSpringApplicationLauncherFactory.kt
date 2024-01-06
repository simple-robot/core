package love.forte.simbot.spring.configuration

import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.spring.application.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * [SimbotSpringApplicationLauncherFactory] 的默认逻辑实现
 * [DefaultSimbotSpringApplicationLauncherFactory] 的配置类。
 *
 */
@Configuration(proxyBeanMethods = true)
public open class DefaultSimbotSpringApplicationLauncherProcessorConfiguration {
    @Bean(DEFAULT_SPRING_APPLICATION_LAUNCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotSpringApplicationLauncherFactory::class)
    public open fun defaultSpringApplicationLauncherProcessor(
        @Autowired(required = false) builderPreConfigurers: List<SimbotSpringApplicationLauncherPreConfigurer>? = null,
        @Autowired(required = false) builderPostConfigurers: List<SimbotSpringApplicationLauncherPostConfigurer>? = null,
        @Autowired(required = false) dispatcherProcessor: SimbotDispatcherProcessor? = null,
        @Autowired(required = false) componentProcessor: SimbotComponentInstallProcessor? = null,
        @Autowired(required = false) pluginProcessor: SimbotPluginInstallProcessor? = null,
    ): DefaultSimbotSpringApplicationLauncherFactory =
        DefaultSimbotSpringApplicationLauncherFactory(
            builderPreConfigurers ?: emptyList(),
            builderPostConfigurers ?: emptyList(),
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
 * [SimbotSpringApplicationLauncherFactory] 的默认实现。
 *
 */
public class DefaultSimbotSpringApplicationLauncherFactory(
    private val builderPreConfigurers: List<SimbotSpringApplicationLauncherPreConfigurer>,
    private val builderPostConfigurers: List<SimbotSpringApplicationLauncherPostConfigurer>,
    private val dispatcherProcessor: SimbotDispatcherProcessor?,
    private val componentProcessor: SimbotComponentInstallProcessor?,
    private val pluginProcessor: SimbotPluginInstallProcessor?,
) : SimbotSpringApplicationLauncherFactory {
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
        // dispatchers
        eventDispatcher {
            dispatcherProcessor?.process(this)
        }

        // components
        componentProcessor?.process(this)
        // plugins
        pluginProcessor?.process(this)
    }
}

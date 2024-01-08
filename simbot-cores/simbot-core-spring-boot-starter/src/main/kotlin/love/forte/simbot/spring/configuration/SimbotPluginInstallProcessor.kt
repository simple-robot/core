package love.forte.simbot.spring.configuration

import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.plugin.PluginFactory
import love.forte.simbot.plugin.PluginInstaller
import love.forte.simbot.plugin.findAnyInstallAllPlugins
import love.forte.simbot.spring.application.SpringApplicationConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 在 starter 中通过 [PluginInstaller] 安装插件信息。
 * 只能存在一个，默认情况下会尝试加载当前所有的
 * [PluginFactory] （无配置注册）、可通过 SPI 自动加载的以及 [SimbotPluginInstaller] 并进行处理。
 *
 * 注册自定义的 [SimbotPluginInstallProcessor] 可覆盖默认行为。
 *
 * @author ForteScarlet
 */
public interface SimbotPluginInstallProcessor {
    /**
     * 处理 [installer].
     */
    public fun process(installer: PluginInstaller)

}

/**
 * 在 starter 中的 [SimbotPluginInstallProcessor] 默认实现
 * [DefaultSimbotPluginInstallProcessor] 行为里对 [PluginInstaller]
 * 进行自定义处理的可扩展类型。
 * 可以注册多个。
 *
 * @see SimbotPluginInstallProcessor
 * @see DefaultSimbotPluginInstallProcessor
 *
 */
public interface SimbotPluginInstaller {
    public fun install(installer: PluginInstaller)
}


/**
 * 配置 [SimbotPluginInstallProcessor] 默认行为实现的配置类。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotPluginInstallProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_PLUGIN_INSTALL_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotPluginInstallProcessor::class)
    public open fun defaultSimbotPluginInstallProcessor(
        properties: SpringApplicationConfigurationProperties,
        @Autowired(required = false) factories: List<PluginFactory<*, *>>? = null,
        @Autowired(required = false) installers: List<SimbotPluginInstaller>? = null,
    ): DefaultSimbotPluginInstallProcessor {
        return DefaultSimbotPluginInstallProcessor(
            properties.plugins.autoInstallProviders,
            properties.plugins.autoInstallProviderConfigurers,
            factories ?: emptyList(),
            installers ?: emptyList()
        )
    }

    public companion object {
        public const val DEFAULT_SIMBOT_PLUGIN_INSTALL_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotPluginInstallProcessor"
    }
}


/**
 * [SimbotPluginInstallProcessor] 的默认行为实现
 */
public class DefaultSimbotPluginInstallProcessor(
    private val autoInstallProviders: Boolean,
    private val autoInstallProviderConfigurers: Boolean,
    private val factories: List<PluginFactory<*, *>>,
    private val installers: List<SimbotPluginInstaller>,
) : SimbotPluginInstallProcessor {
    override fun process(installer: PluginInstaller) {
        if (autoInstallProviders) {
            installer.findAnyInstallAllPlugins(autoInstallProviderConfigurers)
            logger.debug(
                "Automatically install all plugin providers automatically with autoLoadProviderConfigurers={}",
                autoInstallProviderConfigurers
            )
        } else {
            logger.debug("Automatic installation from plugin providers was disabled")
            // 自动的
        }

        // factories
        factories.forEach {
            installer.install(it)
            logger.debug("Installed plugin factory {} of current factories", it)
        }

        // installers
        installers.forEach {
            it.install(installer)
            logger.debug("Installed installer by {}", it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.getLogger(DefaultSimbotPluginInstallProcessor::class)
    }
}

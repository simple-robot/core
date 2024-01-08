package love.forte.simbot.spring.configuration.config

import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.spring.application.SpringApplicationBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * 对 [SpringApplicationBuilder] 进行处理的处理器。
 * 默认逻辑会尝试加载所有的 [SimbotApplicationConfigurationConfigurer]
 * 并使用它们。
 *
 * @author ForteScarlet
 */
public fun interface SimbotApplicationConfigurationProcessor {
    /**
     * 处理 [builder]
     */
    public fun process(builder: SpringApplicationBuilder)
}

/**
 * 在 [SimbotApplicationConfigurationProcessor] 的默认行为中
 * 会被批量加载并依次处理 [SpringApplicationBuilder]
 *
 */
public fun interface SimbotApplicationConfigurationConfigurer {
    /**
     * 配置 [builder]
     */
    public fun configure(builder: SpringApplicationBuilder)
}

/**
 * 用于配置 [SimbotApplicationConfigurationProcessor]
 * 的默认实现 [DefaultSimbotApplicationConfigurationProcessor]
 * 的配置器。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotApplicationConfigurationProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_APPLICATION_CONFIGURATION_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotApplicationConfigurationProcessor::class)
    public open fun defaultSimbotApplicationConfigurationProcessor(
        @Autowired(required = false) configurers: List<SimbotApplicationConfigurationConfigurer>? = null
    ): DefaultSimbotApplicationConfigurationProcessor =
        DefaultSimbotApplicationConfigurationProcessor(configurers ?: emptyList())

    public companion object {
        public const val DEFAULT_SIMBOT_APPLICATION_CONFIGURATION_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.config.defaultSimbotApplicationConfigurationProcessor"
    }
}

/**
 * [SimbotApplicationConfigurationProcessor] 的默认逻辑实现，会使用所有的 [SimbotApplicationConfigurationConfigurer]
 * 并使用它们进行处理。
 *
 */
public open class DefaultSimbotApplicationConfigurationProcessor(
    private val configurers: List<SimbotApplicationConfigurationConfigurer>
) : SimbotApplicationConfigurationProcessor {
    override fun process(builder: SpringApplicationBuilder) {
        configurers.forEach {
            it.configure(builder)
            logger.debug("Configured builder {} by {}", builder, it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotApplicationConfigurationProcessor>()
    }
}

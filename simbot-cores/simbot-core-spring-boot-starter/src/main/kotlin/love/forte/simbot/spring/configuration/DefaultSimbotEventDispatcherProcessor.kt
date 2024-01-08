package love.forte.simbot.spring.configuration

import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 配置 [SimbotEventDispatcherProcessor] 的默认实现
 * [DefaultSimbotEventDispatcherProcessor]
 * 的配置类。
 *
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotEventDispatcherProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_EVENT_DISPATCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotEventDispatcherProcessor::class)
    public open fun defaultSimbotEventDispatcherProcessor(
        registerProcessor: SimbotEventListenerRegistrarProcessor,
        @Autowired(required = false) postConfigurers: List<SimbotEventDispatcherPostConfigurer>? = null
    ): DefaultSimbotEventDispatcherProcessor = DefaultSimbotEventDispatcherProcessor(
        registerProcessor,
        postConfigurers ?: emptyList()
    )

    public companion object {
        public const val DEFAULT_SIMBOT_EVENT_DISPATCHER_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotEventDispatcherProcessor"
    }
}

/**
 * [SimbotEventDispatcherProcessor] 的默认行为实现。
 * @author ForteScarlet
 */
public open class DefaultSimbotEventDispatcherProcessor(
    private val registerProcessor: SimbotEventListenerRegistrarProcessor,
    private val postConfigurers: List<SimbotEventDispatcherPostConfigurer>
) : SimbotEventDispatcherProcessor {
    override fun process(dispatcher: EventDispatcher) {
        registerProcessor.process(dispatcher)
        logger.debug("Processed dispatcher {} by processor {}", dispatcher, registerProcessor)

        postConfigurers.forEach {
            it.configure(dispatcher)
            logger.debug("Configured dispatcher {} by {}", dispatcher, it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotDispatcherProcessor>()
    }
}

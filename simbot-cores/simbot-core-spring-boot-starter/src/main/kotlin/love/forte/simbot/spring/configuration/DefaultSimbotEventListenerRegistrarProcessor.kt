package love.forte.simbot.spring.configuration

import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerRegistrar
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * 配置 [SimbotEventListenerRegistrarProcessor]
 * 的默认行为实现类型 [DefaultSimbotEventListenerRegistrarProcessor]
 * 的配置类。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotEventListenerRegistrarProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_EVENT_LISTENER_REGISTRAR_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotEventListenerRegistrarProcessor::class)
    public open fun defaultSimbotEventListenerRegistrarProcessor(
        @Autowired(required = false) listenerInstances: List<EventListener>? = null,
        @Autowired(required = false) postConfigurers: List<SimbotEventListenerRegistrarPostConfigurer>? = null,
    ): DefaultSimbotEventListenerRegistrarProcessor =
        DefaultSimbotEventListenerRegistrarProcessor(
            listenerInstances = listenerInstances ?: emptyList(),
            postConfigurers ?: emptyList()
        )

    public companion object {
        public const val DEFAULT_SIMBOT_EVENT_LISTENER_REGISTRAR_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotEventListenerRegistrarProcessor"
    }
}

/**
 * [SimbotEventListenerRegistrarProcessor] 的默认行为实现类
 * @author ForteScarlet
 */
public open class DefaultSimbotEventListenerRegistrarProcessor(
    private val listenerInstances: List<EventListener>,
    private val postConfigurers: List<SimbotEventListenerRegistrarPostConfigurer>,
) : SimbotEventListenerRegistrarProcessor {
    override fun process(registrar: EventListenerRegistrar) {
        listenerInstances.forEach {
            registrar.register(it)
            logger.debug("Registered event listener {}", it)
        }

        postConfigurers.forEach {
            it.configure(registrar)
            logger.debug("Configured event registrar by {}", it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotEventListenerRegistrarProcessor>()
    }
}

package love.forte.simbot.spring.configuration

import love.forte.simbot.spring.application.SpringApplicationConfigurationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 *
 * @author ForteScarlet
 */
@Configuration(proxyBeanMethods = false)
public open class SimbotSpringPropertiesConfiguration {
    /**
     * Simbot spring properties
     */
    @Bean
    @ConditionalOnMissingBean(SpringApplicationConfigurationProperties::class)
    @ConfigurationProperties("simbot")
    public open fun springApplicationConfigurationProperties(): SpringApplicationConfigurationProperties {
        return SpringApplicationConfigurationProperties()
    }
}

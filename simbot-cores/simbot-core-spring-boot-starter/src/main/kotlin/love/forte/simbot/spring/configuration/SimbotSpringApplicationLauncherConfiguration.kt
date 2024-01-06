package love.forte.simbot.spring.configuration

import love.forte.simbot.spring.application.Spring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 *
 * @author ForteScarlet
 */
@Configuration(proxyBeanMethods = false)
public open class SimbotSpringApplicationLauncherConfiguration {

    @Bean
    public open fun a(processor: SimbotSpringApplicationLauncherFactory) {
        val launcher = processor.process(Spring)
    }

}




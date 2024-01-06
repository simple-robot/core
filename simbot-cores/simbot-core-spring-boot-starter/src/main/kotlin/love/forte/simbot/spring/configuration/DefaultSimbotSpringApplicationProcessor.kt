package love.forte.simbot.spring.configuration

import love.forte.simbot.spring.application.SpringApplication
import love.forte.simbot.spring.application.SpringApplicationConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotSpringApplicationProcessorConfiguration {
    // TODO

}


// TODO 加载所有的binder配置?
//  binder?

/**
 * [SimbotSpringApplicationProcessor] 的默认实现，
 * 默认行为中会进行如下内容：
 * - 扫描、加载事件处理器并注册
 * - 扫描、加载所有的可注册 bot
 *
 * @author ForteScarlet
 */
public class DefaultSimbotSpringApplicationProcessor(
    private val properties: SpringApplicationConfigurationProperties,
) : SimbotSpringApplicationProcessor {
    override fun process(application: SpringApplication) {
        TODO("Not yet implemented")
    }
}

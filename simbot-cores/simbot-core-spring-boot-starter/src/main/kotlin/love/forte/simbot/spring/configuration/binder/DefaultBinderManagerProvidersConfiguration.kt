package love.forte.simbot.spring.configuration.binder

import love.forte.simbot.quantcat.annotations.FilterValue
import love.forte.simbot.quantcat.annotations.toProperties
import love.forte.simbot.quantcat.common.binder.impl.EventParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.impl.KeywordBinderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.full.findAnnotation

/**
 * 用以注册部分默认提供的 Spring 环境下全局配置的绑定器配置。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultBinderManagerProvidersConfiguration {
    @Bean
    public open fun eventParameterBinderFactory(): EventParameterBinderFactory = EventParameterBinderFactory

    @Bean
    public open fun keywordBinderFactory(): KeywordBinderFactory = KeywordBinderFactory { context ->
        context.parameter.findAnnotation<FilterValue>()?.toProperties()
    }
}

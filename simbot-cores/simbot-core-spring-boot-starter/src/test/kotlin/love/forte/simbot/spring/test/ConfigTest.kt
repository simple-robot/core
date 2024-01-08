package love.forte.simbot.spring.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component


@Component
open class Runner(@Autowired(required = false) val provider: MyProvider?) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("provider: $provider")
        provider?.run()
    }
}


@Import(DefaultMyProviderConfiguration::class)
annotation class EnableIncludeTest

interface MyProvider {
    fun run()
}

@Configuration
@Import(DefaultSubConfiguration::class)
@ConditionalOnMissingBean(MyProvider::class)
open class DefaultMyProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean(MyProvider::class)
    open fun defaultProvider(): DefaultMyProvider = DefaultMyProvider
}

object DefaultMyProvider : MyProvider {
    override fun run() {
        println("DEFAULT PROVIDER!")
    }
}

@Configuration(proxyBeanMethods = false)
open class DefaultSubConfiguration {
    @Bean
    open fun hello() = Hello("forte")
}

data class Hello(val name: String)

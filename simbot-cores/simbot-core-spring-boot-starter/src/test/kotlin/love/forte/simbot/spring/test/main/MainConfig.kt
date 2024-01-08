package love.forte.simbot.spring.test.main

import love.forte.simbot.spring.test.EnableIncludeTest
import love.forte.simbot.spring.test.Hello
import love.forte.simbot.spring.test.MyProvider
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


/**
 *
 * @author ForteScarlet
 */
@SpringBootApplication
@EnableIncludeTest
open class MainConfigApp

fun main() {
    runApplication<MainConfigApp>()
}

@Component
open class Runner(
    @Autowired(required = false) private val provider: MyProvider?,
    @Autowired(required = false) private val hello: Hello?
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        Assertions.assertNotNull(provider)
        Assertions.assertNotNull(hello)
    }
}


@Configuration(proxyBeanMethods = false)
open class OtherProviderConfiguration {
    @Bean
    open fun otherProvider(): MyProvider = object : MyProvider {
        override fun run() {
            println("Other Provider!")
        }

    }
}

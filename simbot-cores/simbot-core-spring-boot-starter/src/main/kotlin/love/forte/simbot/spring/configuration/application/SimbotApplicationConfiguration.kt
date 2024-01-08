package love.forte.simbot.spring.configuration.application

import love.forte.simbot.spring.application.SpringApplication
import love.forte.simbot.spring.application.SpringApplicationLauncher
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 *
 * @author ForteScarlet
 */
@Configuration(proxyBeanMethods = false)
public open class SimbotApplicationConfiguration {
    @Bean(SPRING_APPLICATION_LAUNCHER_BEAN_NAME)
    public open fun springApplicationLauncher(
        factory: SimbotApplicationLauncherFactory,
        processor: SimbotApplicationLauncherFactoryProcessor
    ): SpringApplicationLauncher =
        processor.process(factory)

    @Bean(
        value = [SPRING_APPLICATION_BEAN_NAME],
        destroyMethod = "cancel"
    )
    public open fun springApplication(launcher: SpringApplicationLauncher): SpringApplication = runInNoScopeBlocking {
        launcher.launch()
    }

    public companion object {
        public const val SPRING_APPLICATION_LAUNCHER_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSpringApplicationLauncher"

        public const val SPRING_APPLICATION_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSpringApplication"
    }
}

package love.forte.simbot.spring.test

import kotlinx.coroutines.delay
import love.forte.simbot.application.ApplicationLauncher
import love.forte.simbot.quantcat.annotations.Listener
import love.forte.simbot.quantcat.common.binder.BinderManager
import love.forte.simbot.spring.EnableSimbot
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerResolver
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component


@SpringBootTest(
    classes = [FunctionalBindableEventListenerTests::class, ListenerContainer::class]
)
@EnableSimbot
class FunctionalBindableEventListenerTests {


    @Test
    fun test(
        @Autowired launcher: ApplicationLauncher<*>,
        @Autowired binderManager: BinderManager,
        @Autowired(required = false) eventListenerResolvers: List<SimbotEventListenerResolver>?
    ) {
        println(binderManager)
        println(eventListenerResolvers)
        println(launcher)

        // eventListenerResolvers?.forEach { r ->
        //     r.resolve(launchSimpleApplication {  })
        // }
    }

}

@Component
private class ListenerContainer {
    @Listener
    suspend fun runner() {
        delay(1)
    }
}

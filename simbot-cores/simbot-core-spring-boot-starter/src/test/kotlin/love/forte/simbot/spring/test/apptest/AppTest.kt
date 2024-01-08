package love.forte.simbot.spring.test.apptest

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.application.Application
import love.forte.simbot.application.onLaunch
import love.forte.simbot.common.id.UUID
import love.forte.simbot.event.Event
import love.forte.simbot.event.throwIfError
import love.forte.simbot.plugin.PluginInstaller
import love.forte.simbot.plugin.createPlugin
import love.forte.simbot.quantcat.annotations.Listener
import love.forte.simbot.spring.EnableSimbot
import love.forte.simbot.spring.configuration.SimbotPluginInstaller
import love.forte.simbot.test.event.TestEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import kotlin.coroutines.resume

@EnableSimbot
@SpringBootApplication
open class AppTestMain

private val eventChannel = Channel<Event> { }
private val continuationDeferred = CompletableDeferred<CancellableContinuation<Int>>()

private const val VALUE = 1

/**
 *
 * @author ForteScarlet
 */
@SpringBootTest(
    classes = [AppTestMain::class, EventListener::class, TestPluginInstaller::class],
    properties = [
        "logging.level.love.forte.simbot=DEBUG"
    ]
)
class AppTest {

    @Autowired(required = false)
    lateinit var application: Application

    @Test
    fun launchApplication() {
        runTest {
            val resumed = suspendCancellableCoroutine {
                continuationDeferred.complete(it)
                application.launch { eventChannel.send(TestEvent(UUID.random())) }
            }

            Assertions.assertEquals(VALUE, resumed)
        }
    }
}

@Component
private class TestPluginInstaller : SimbotPluginInstaller {
    @OptIn(ExperimentalSimbotAPI::class)
    override fun install(installer: PluginInstaller) {
        val plugin = createPlugin("spring.test") {
            val dispatcher = eventDispatcher
            applicationEventRegistrar.onLaunch { app ->
                app.launch {
                    eventChannel.consumeEach { event ->
                        println("Consume!")
                        app.launch {
                            dispatcher.push(event)
                                .throwIfError()
                                .catch { e -> e.printStackTrace() }
                                .collect { result ->
                                    println("Event result: $result")
                                }
                        }
                    }
                }
            }
        }

        installer.install(plugin)
    }
}


@Component
private class EventListener(private val application: Application) {

    @Listener
    private suspend fun Event.handle(name: String? = "forte") {
        println("Event.id: $id")
        Assertions.assertEquals("forte", name)
        println("On Event: $this, app: $application")
        val continuation = continuationDeferred.await()
        suspendCancellableCoroutine {
            it.resume(Unit)
            continuation.resume(VALUE)
        }
    }
}

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.takeWhileNotError
import java.util.concurrent.Executors
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class AppTests {
    private val d1 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D1t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    private val d2 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D2t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    @Test
    fun appTest() = runTest {
        val app = launchSimpleApplication {
            eventDispatcher {
                coroutineContext += d1
                addDispatchInterceptor {

                    flow {
                        emit(EventResult.of(1))
                        emit(EventResult.of(2))
                        emit(EventResult.of(3))
                        emit(EventResult.of(4))
                        emit(EventResult.of(5))
                    }
                }
            }
        }

        val eventDispatcher = app.eventDispatcher

        eventDispatcher.register {
            println("[${Thread.currentThread()}] Listener $0")
            EventResult.of(0)
        }

        eventDispatcher.register {
            println("[${Thread.currentThread()}] Listener $1")
            throw RuntimeException("Listener 1 error")
        }

        eventDispatcher.register {
            println("[${Thread.currentThread()}] Listener $2")
            EventResult.of(2)
        }

        eventDispatcher.push(TestEvent())
            .onEach {
                println("[${Thread.currentThread()}]: onEach1: $it")
            }
            .takeWhileNotError()
            .collect {
                println("[${Thread.currentThread()}]: collect: $it")
            }

        app.cancel()
    }


    fun test2() = runTest {
        val application = launchSimpleApplication {
            eventDispatcher {
                // ....
            }
            config {
                this.coroutineContext
            }
        }

    }
}


private class TestEvent : Event {
    override val id: ID = UUID.random()
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()
}

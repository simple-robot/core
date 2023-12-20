package love.forte.simbot.core.application

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.application.launchApplication
import love.forte.simbot.application.onCancelled
import love.forte.simbot.application.onLaunch
import love.forte.simbot.application.onRequestCancel
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.pushAndCollect
import love.forte.simbot.plugin.createPlugin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 *
 * @author ForteScarlet
 */
class SimpleApplicationTests {

    @Test
    fun simpleApplicationTest() = runTest {
        val numValue = 5
        val nameValue = "forte"

        var launched = false
        var requestCancel = false
        var cancelled = false

        val app = launchApplication(Simple) {
            config {
                coroutineContext += CoroutineName("TEST-SIMPLE")
            }

            eventDispatcher {
                coroutineContext = CoroutineName("TEST-DIS")

                addDispatchInterceptor {
                    it.invoke()
                }

                addInterceptor({
                    priority = 1
                }) {
                    println("context1: $it")
                    println("context1.eventListenerContext: ${it.eventListenerContext}")
                    val res = it.invoke()
                    println("context1.end: $it")
                    println("context1.end.eventListenerContext: ${it.eventListenerContext}")
                    res
                }
                addInterceptor({
                    priority = 2
                }) {
                    println("context2: $it")
                    println("context2.eventListenerContext: ${it.eventListenerContext}")
                    val newEventListenerContext = object : EventListenerContext by it.eventListenerContext {
                        override fun toString(): String = "DeleteEventListenerContext(${it.eventListenerContext})"
                    }
                    println("newEventListenerContext: $newEventListenerContext")
                    val res = it.invoke(newEventListenerContext)
                    println("context2.end: $it")
                    println("context2.end.eventListenerContext: ${it.eventListenerContext}")
                    res
                }
                addInterceptor({
                    priority = 3
                }) {
                    println("context3: $it")
                    println("context3.eventListenerContext: ${it.eventListenerContext}")
                    val res = it.invoke()
                    println("context3.end: $it")
                    println("context3.end.eventListenerContext: ${it.eventListenerContext}")
                    res
                }

                addInterceptor({
                    priority = 1
                }) {
                    it.invoke()
                }
                addDispatchInterceptor({
                    priority = 1
                }
                ) {
                    it.invoke()
                }
                addInterceptor({
                    priority = 1
                }) {
                    it.invoke()
                }
                addInterceptor({
                    priority = 1
                }) {
                    it.invoke()
                }
            }

            stageEvents {
                onLaunch {
                    launched = true
                }
                onRequestCancel {
                    requestCancel = true
                }
                onCancelled {
                    cancelled = true
                }
            }

            install(TestPlugin) {
                assertEquals(num, 10)
                assertEquals(name, "forliy")
            }

            install(TestPlugin) {
                num = numValue
            }

            install(TestPlugin) {
                name = nameValue
            }

            install(TestPlugin) {
                assertEquals(num, numValue)
                assertEquals(name, nameValue)
            }
        }

        println(app)

        val coroutineName = app.coroutineContext[CoroutineName]
        assertNotNull(coroutineName)
        assertEquals(coroutineName.name, "TEST-SIMPLE")

        val myPlugin = app.plugins.first()
        println(myPlugin)

        app.eventDispatcher.register { context ->
            println("Context: $context")
            println("Context.context: ${context.context}")
            println("Event: ${context.event}")
            EventResult.of()
        }
        app.eventDispatcher.pushAndCollect(TestEvent()) {
            println("EventResult: $it")
        }

        app.cancel()
        app.join()

        assertEquals(app.isActive, false)

        assertEquals(launched, true)
        assertEquals(requestCancel, true)
        assertEquals(cancelled, true)

    }
}

private class TestEvent : Event {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalAPI::class)
    override val time: Timestamp = Timestamp.now()
}

private class TestPluginConf {
    var num: Int = 10
    var name: String = "forliy"
    override fun toString(): String {
        return "TestPluginConf(num=$num, name='$name')"
    }

}

@OptIn(ExperimentalAPI::class)
private val TestPlugin = createPlugin("TestPlugin", ::TestPluginConf) { conf ->
    applicationEventRegistrar.onLaunch {
        println("Launch!")
    }
    applicationEventRegistrar.onCancelled {
        assertEquals(it.isActive, false)
    }
    applicationEventRegistrar.onRequestCancel {
        assertEquals(it.isActive, true)
    }

}



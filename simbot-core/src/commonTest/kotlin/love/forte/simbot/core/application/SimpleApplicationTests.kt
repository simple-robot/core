package love.forte.simbot.core.application

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.application.launchApplication
import love.forte.simbot.application.onCancelled
import love.forte.simbot.application.onLaunch
import love.forte.simbot.application.onRequestCancel
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
                addInterceptor {
                    it.invoke()
                }
                addDispatchInterceptor {
                    it.invoke()
                }
                addInterceptor {
                    it.invoke()
                }
                addInterceptor {
                    it.invoke()
                }

                addInterceptor({
                    it.invoke()
                }) {
                    priority = 1
                }
                addDispatchInterceptor(
                    { it.invoke() }
                ) {
                    priority = 1
                }
                addInterceptor({
                    it.invoke()
                }) {
                    priority = 1
                }
                addInterceptor({
                    it.invoke()
                }) {
                    priority = 1
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

        app.cancel()
        app.join()

        assertEquals(app.isActive, false)

        assertEquals(launched, true)
        assertEquals(requestCancel, true)
        assertEquals(cancelled, true)

    }
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



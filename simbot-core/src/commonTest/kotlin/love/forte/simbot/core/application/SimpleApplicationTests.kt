package love.forte.simbot.core.application

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import love.forte.simbot.application.*
import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.event.addEachScopeInterceptor
import love.forte.simbot.event.addGlobalScopeInterceptor
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeBy
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
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
                addInterceptor(EventInterceptor.Scope.GLOBAL) {
                    it.invoke()
                }
                addGlobalScopeInterceptor {
                    it.invoke()
                }
                addInterceptor(EventInterceptor.Scope.EACH) {
                    it.invoke()
                }
                addEachScopeInterceptor {
                    it.invoke()
                }

                addInterceptor(EventInterceptor.Scope.GLOBAL, {
                    it.invoke()
                }) {
                    priority = 1
                }
                addGlobalScopeInterceptor(
                    { it.invoke() }
                ) {
                    priority = 1
                }
                addInterceptor(EventInterceptor.Scope.EACH, {
                    it.invoke()
                }) {
                    priority = 1
                }
                addEachScopeInterceptor({
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

            install(MyPlugin) {
                num = numValue
            }

            install(MyPlugin) {
                name = nameValue
            }
        }

        println(app)

        val coroutineName = app.coroutineContext[CoroutineName]
        assertNotNull(coroutineName)
        assertEquals(coroutineName.name, "TEST-SIMPLE")

        val myPlugin = app.plugins.get<MyPlugin>()

        assertEquals(myPlugin.conf.num, numValue)
        assertEquals(myPlugin.conf.name, nameValue)

        app.cancel()
        app.join()

        assertEquals(app.isActive, false)

        assertEquals(launched, true)
        assertEquals(requestCancel, true)
        assertEquals(cancelled, true)

    }
}


private data class MyPlugin(val conf: Conf) : Plugin {

    data class Conf(var num: Int, var name: String)

    companion object Factory : PluginFactory<MyPlugin, Conf> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<Conf>): MyPlugin {
            val conf = Conf(0, "").invokeBy(configurer)
            val myPlugin = MyPlugin(conf)
            context.applicationEventRegistrar.onLaunch {
                println("Launch! MyPlugin is $myPlugin")
            }
            context.applicationEventRegistrar.onCancelled {
                assertEquals(it.isActive, false)
            }
            context.applicationEventRegistrar.onRequestCancel {
                assertEquals(it.isActive, true)
            }

            return myPlugin
        }

    }
}

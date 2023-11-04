package love.forte.simbot.plugin

import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeBy


// TODO

private data class PluginKey(val name: String) : PluginFactory.Key

private data class SimplePlugin<CONF>(val configuration: CONF) : Plugin

@PublishedApi
internal fun nameBasedPluginKey(name: String): PluginFactory.Key = PluginKey(name)

@PublishedApi
internal fun <CONF : Any> simplePlugin(conf: CONF): Plugin = SimplePlugin(conf)

@ExperimentalAPI
public inline fun <CONF : Any> createPlugin(
    name: String,
    crossinline configCreator: () -> CONF,
    crossinline creator: PluginConfigureContext.(CONF) -> Unit
): PluginFactory<Plugin, CONF> {
    val key = nameBasedPluginKey(name)

    return object : PluginFactory<Plugin, CONF> {
        override val key: PluginFactory.Key = key
        override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): Plugin {
            val conf = configCreator().invokeBy(configurer)
            context.creator(conf)
            return simplePlugin(conf)
        }
    }
}


@OptIn(ExperimentalAPI::class)
public inline fun createPlugin(
    name: String,
    crossinline creator: PluginConfigureContext.(Unit) -> Unit
): PluginFactory<Plugin, Unit> = createPlugin(name, { }, creator)





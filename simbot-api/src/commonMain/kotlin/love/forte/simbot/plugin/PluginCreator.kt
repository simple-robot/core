package love.forte.simbot.plugin

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy


// TODO

public interface SimplePlugin : Plugin {
    public val key: Key

    public interface Key : PluginFactory.Key {
        public val name: String
    }
}

private data class PluginKey(override val name: String) : SimplePlugin.Key

private data class SimplePluginImpl<CONF>(override val key: SimplePlugin.Key, val configuration: CONF) : SimplePlugin

@PublishedApi
internal fun nameBasedPluginKey(name: String): SimplePlugin.Key = PluginKey(name)

@PublishedApi
internal fun <CONF : Any> simplePlugin(key: SimplePlugin.Key, conf: CONF): SimplePlugin = SimplePluginImpl(key, conf)

@ExperimentalSimbotAPI
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
            return simplePlugin(key, conf)
        }
    }
}


@ExperimentalSimbotAPI
public inline fun createPlugin(
    name: String,
    crossinline creator: PluginConfigureContext.(Unit) -> Unit
): PluginFactory<Plugin, Unit> = createPlugin(name, { }, creator)





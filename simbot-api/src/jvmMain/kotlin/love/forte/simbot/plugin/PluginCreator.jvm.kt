package love.forte.simbot.plugin

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy


public abstract class AbstractJPlugin(override val key: Key) : SimplePlugin {

    public interface Key : SimplePlugin.Key {
        override val name: String
    }

    /**
     * 应实现为单例。
     *
     */
    public abstract class Factory<P : AbstractJPlugin, CONF : Any>(name: String) : PluginFactory<P, CONF> {
        override val key: PluginFactory.Key = createKey(name)

        protected abstract fun createConfig(): CONF
        protected abstract fun create(context: PluginConfigureContext, configuration: CONF): P

        final override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): P {
            val conf = createConfig().invokeBy(configurer)
            return create(context, conf)
        }
    }

    public companion object {
        private data class NamedKey(override val name: String) : Key

        @JvmStatic
        public fun createKey(name: String): Key = NamedKey(name)
    }
}



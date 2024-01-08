package love.forte.simbot.test.plugin

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory


/**
 * 用于测试的 [Plugin] 实现，没有什么具体的功能。
 *
 * @author ForteScarlet
 */
public open class TestPlugin(public val configuration: TestPluginConfiguration) : Plugin {

    public companion object Factory : PluginFactory<TestPlugin, TestPluginConfiguration> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<TestPluginConfiguration>
        ): TestPlugin {
            val configuration = TestPluginConfiguration().invokeBy(configurer)
            return TestPlugin(configuration)
        }
    }

}

/**
 * [TestPlugin] 的配置类。
 */
public open class TestPluginConfiguration


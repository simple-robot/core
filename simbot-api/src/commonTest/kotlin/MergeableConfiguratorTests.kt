import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.application.Plugin
import love.forte.simbot.application.PluginConfigureContext
import love.forte.simbot.application.PluginFactoriesConfigurator
import love.forte.simbot.application.PluginFactory
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.utils.MergeableFactory
import love.forte.simbot.utils.invokeWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class MergeableConfiguratorTests {

    private class TestComponent private constructor() : Component {
        override val id: String = "simbot.test"
        override val serializersModule: SerializersModule = EmptySerializersModule()

        companion object Factory : ComponentFactory<TestComponent, Unit> {
            override val key: ComponentFactory.Key = object : ComponentFactory.Key {}


            override fun create(configurer: MergeableFactory.Configurer<Unit>): TestComponent {
                configurer.invokeWith(Unit)
                return TestComponent()
            }
        }
    }

    private class TestPlugin private constructor() : Plugin {
        companion object Factory : PluginFactory<TestPlugin, Unit> {
            override val key: PluginFactory.Key = object : PluginFactory.Key {}


            override fun create(configurer: MergeableFactory.Configurer<Unit>): TestPlugin {
                configurer.invokeWith(Unit)
                return TestPlugin()
            }
        }
    }

    @Test
    fun mergeable_configuration_component_test() {
        var count = 0
        val configurator = ComponentFactoriesConfigurator().apply {
            add(TestComponent) {
                count++
            }
            add(TestComponent) {
                count++
            }
        }

        val context = object : ComponentConfigureContext {}

        configurator.create(context, TestComponent)

        assertEquals(count, 2)
    }

    @Test
    fun mergeable_configuration_plugin_test() {
        var count = 0
        val configurator = PluginFactoriesConfigurator().apply {
            add(TestPlugin) {
                count++
            }
            add(TestPlugin) {
                count++
            }
        }

        val context = object : PluginConfigureContext {}

        configurator.create(context, TestPlugin)

        assertEquals(count, 2)
    }

}

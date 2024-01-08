package love.forte.simbot.test.component

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 *
 * @author ForteScarlet
 */
public class TestComponent(public val configuration: TestComponentConfiguration) : Component {
    override val id: String
        get() = ID_VALUE

    override val serializersModule: SerializersModule
        get() = configuration.serializersModule

    public companion object Factory : ComponentFactory<TestComponent, TestComponentConfiguration> {
        public const val ID_VALUE: String = "simbot.test"
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        @get:JvmStatic
        @get:JvmName("serializersModule")
        public val serializersModule: SerializersModule = EmptySerializersModule()

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<TestComponentConfiguration>
        ): TestComponent {
            val configuration = TestComponentConfiguration().invokeBy(configurer)
            return TestComponent(configuration)
        }
    }
}

/**
 * Configuration of [TestComponent]
 */
public open class TestComponentConfiguration {
    public open var serializersModule: SerializersModule = EmptySerializersModule()
}

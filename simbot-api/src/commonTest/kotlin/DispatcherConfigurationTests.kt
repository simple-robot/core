import kotlinx.serialization.json.Json
import love.forte.simbot.bot.configuration.DispatcherConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 *
 * @author ForteScarlet
 */
class DispatcherConfigurationTests {

    @Test
    fun demoteTest() {
        val jsonStr = """
            {
                "type": "j21_virtual",
                "demote": {
                    "type": "custom",
                    "coreThreads": 4,
                    "demote": {
                        "type": "io",
                        "demote": {
                            "type": "custom",
                            "coreThreads": 8,
                            "demote": {
                                "type": "default"
                            }
                        }
                    }
                }
            }
        """.trimIndent()

        // virtual -> custom -> io -> custom -> default

        val json = Json { isLenient = true; ignoreUnknownKeys = true }

        val config = json.decodeFromString(DispatcherConfiguration.serializer(), jsonStr)

        assertIs<DispatcherConfiguration.Virtual>(config)
        val virtualDemote = config.demote
        assertIs<DispatcherConfiguration.Custom>(virtualDemote)
        val customDemote = virtualDemote.demote
        assertIs<DispatcherConfiguration.IO>(customDemote)
        val ioDemote = customDemote.demote
        assertIs<DispatcherConfiguration.Custom>(ioDemote)
        val custom1Demote = ioDemote.demote
        assertIs<DispatcherConfiguration.Default>(custom1Demote)

        val config0 = DispatcherConfiguration.Virtual(
            demote = DispatcherConfiguration.Custom(
                coreThreads = 4,
                demote = DispatcherConfiguration.IO(
                    DispatcherConfiguration.Custom(
                        coreThreads = 8,
                        demote = DispatcherConfiguration.Default
                    )
                )
            )
        )

        assertEquals(config0, config)

        println(config)
    }

}

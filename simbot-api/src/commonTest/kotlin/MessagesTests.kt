import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import love.forte.simbot.id.IntID.Companion.ID
import love.forte.simbot.id.StringID.Companion.ID
import love.forte.simbot.id.UUID
import love.forte.simbot.message.*
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.resource.toResource
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagesTests {

    @Test
    fun standardMessageSerializationTest() {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            serializersModule = Messages.standardSerializersModule
            prettyPrint = true
        }

        val messages = buildMessages {
            add("Hello".toText())
            add(AtAll)
            add(At(1.ID))
            add(UUID.random().toString().toByteArray().toResource().toOfflineImage())
            add(UUID.random().toString().toByteArray().toResource().toOfflineResourceImage())
            add(Face("FACE".ID))
            add(Emoji(UUID.random()))
            add(RemoteIDImage(UUID.random()))

            addIntoMessages()

            add("World".toText())
        }

        val jsonStr = json.encodeMessagesToString(messages)

        println(jsonStr)

        val decodedMessages = json.decodeMessagesFromString(jsonStr)

        assertEquals(messages.size, decodedMessages.size)
        assertEquals(messages, decodedMessages)
    }


}

internal expect fun MessagesBuilder.addIntoMessages()

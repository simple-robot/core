import love.forte.simbot.common.id.UUID
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class UUIDTests {

    @Test
    fun uuidTest() {
        val uuid = UUID.random()
        val javaUUID = java.util.UUID(uuid.mostSignificantBits, uuid.leastSignificantBits)

        println(uuid)
        println(javaUUID)

        println(uuid.toString() == javaUUID.toString())
        println(uuid.toString() == javaUUID.toString())
        println(uuid.toString() == javaUUID.toString())

    }

}

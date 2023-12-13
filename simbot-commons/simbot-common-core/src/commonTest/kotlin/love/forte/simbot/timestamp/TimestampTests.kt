package love.forte.simbot.timestamp

import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.utils.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class TimestampTests {

    @Test
    fun millisTests() {
        val millis = 1697822131902L
        assertEquals(Timestamp.ofMilliseconds(millis), Timestamp.ofMilliseconds(millis))

        val timestamp = MillisecondsTimestamp(millis)
        assertEquals(timestamp.timeAs(TimeUnit.SECONDS), millis / 1000)
    }

    @OptIn(ExperimentalAPI::class)
    @Test
    fun nowTimestampTests() {
        val now = Timestamp.now()

        println(now)

        assertEquals(now.compareTo(now), 0)
        assertEquals(now.milliseconds.toString().length, 13)

        val timeOfNow = Timestamp.ofMilliseconds(now.milliseconds)

        assertEquals(now, timeOfNow)
        assertEquals(now.milliseconds, timeOfNow.milliseconds)
        assertEquals(now.compareTo(timeOfNow), 0)

    }

}

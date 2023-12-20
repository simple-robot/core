package love.forte.simbot.timestamp

import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.common.time.DateTimestamp
import love.forte.simbot.common.time.Timestamp
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class DateTimestampTests {

    @Test
    fun jsDateTimestampTest() {
        val timestamp = DateTimestamp(Date())

        // is milliseconds (13位)
        assertTrue { timestamp.milliseconds.toString().length >= 13 }

        Date().let { d ->
            assertEquals(DateTimestamp(d), DateTimestamp(d))
        }
    }

    @OptIn(ExperimentalAPI::class)
    @Test
    fun jsTimestampNowIsDateTimestampTest() {
        assertTrue("JS Timestamp.now is not DateTimestamp instance") { Timestamp.now() is DateTimestamp }
    }

}

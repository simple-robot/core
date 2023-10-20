package love.forte.simbot.timestamp

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

    @Test
    fun jsTimestampNowIsDateTimestampTest() {
        assertTrue("JS Timestamp.now is not DateTimestamp instance") { Timestamp.now() is DateTimestamp }
    }

}

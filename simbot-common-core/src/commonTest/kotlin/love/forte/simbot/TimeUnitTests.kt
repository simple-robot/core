package love.forte.simbot

import love.forte.simbot.utils.TimeUnit
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [TimeUnit].
 *
 */
class TimeUnitTests {

    @Test
    fun timeUnitConvertToSelfTest() {
        fun TimeUnit.doConvert(duration: Long, block: TimeUnit.(Long) -> Long) {
            assertEquals(block(duration), duration, "${this}.doConvert(duration, $block) not return it self value")
        }

        TimeUnit.MICROSECONDS.doConvert(Random.nextLong(), TimeUnit::toMicros)
        TimeUnit.NANOSECONDS.doConvert(Random.nextLong(), TimeUnit::toNanos)
        TimeUnit.MILLISECONDS.doConvert(Random.nextLong(), TimeUnit::toMillis)
        TimeUnit.SECONDS.doConvert(Random.nextLong(), TimeUnit::toSeconds)
        TimeUnit.MINUTES.doConvert(Random.nextLong(), TimeUnit::toMinutes)
        TimeUnit.HOURS.doConvert(Random.nextLong(), TimeUnit::toHours)
        TimeUnit.DAYS.doConvert(Random.nextLong(), TimeUnit::toDays)

        fun TimeUnit.doConvert(duration: Long) {
            assertEquals(duration, convert(duration, this))
        }

        TimeUnit.NANOSECONDS.doConvert(Random.nextLong())
        TimeUnit.MICROSECONDS.doConvert(Random.nextLong())
        TimeUnit.MILLISECONDS.doConvert(Random.nextLong())
        TimeUnit.SECONDS.doConvert(Random.nextLong())
        TimeUnit.MINUTES.doConvert(Random.nextLong())
        TimeUnit.HOURS.doConvert(Random.nextLong())
        TimeUnit.DAYS.doConvert(Random.nextLong())
    }

    // todo convert test, to xxx test


}

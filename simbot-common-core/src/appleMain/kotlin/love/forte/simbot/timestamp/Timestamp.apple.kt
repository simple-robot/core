package love.forte.simbot.timestamp

import kotlinx.cinterop.UnsafeNumber
import love.forte.simbot.annotations.ExperimentalAPI
import love.forte.simbot.timestamp.NSDateTimestamp.Companion.toTimestamp
import platform.Foundation.NSDate
import platform.Foundation.compare
import platform.Foundation.now
import platform.Foundation.timeIntervalSince1970


/**
 * 基于 [NSDate] 的 [Timestamp] 实现。
 *
 * @see Timestamp
 */
public class NSDateTimestamp(public val date: NSDate) : Timestamp {
    /**
     * 得到 [date] 对应的 epoch 毫秒时间戳。
     *
     * 通过 [NSDate.timeIntervalSince1970] 计算得到。
     *
     * 计算方式：
     * ```kotlin
     * (date.timeIntervalSince1970() * 1000).toLong()
     * ```
     */
    override val milliseconds: Long
        get() = (date.timeIntervalSince1970() * 1000).toLong()

    @OptIn(UnsafeNumber::class)
    override fun compareTo(other: Timestamp): Int {
        if (other is NSDateTimestamp) {
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            return date.compare(other.date).toInt()
        }

        return milliseconds.compareTo(other.milliseconds)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false
        if (other is NSDateTimestamp) return date == other.date

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = date.hashCode()
    override fun toString(): String = "NSDateTimestamp(milliseconds=$milliseconds, date=$date)"


    public companion object {

        /**
         * 通过 [NSDate] 获取 [Timestamp]。
         */
        public fun NSDate.toTimestamp(): Timestamp = NSDateTimestamp(this)

    }
}


/**
 * 得到一个记录了当前 epoch 时间的 Timestamp 实例。
 *
 * 通过 [NSDate.timeIntervalSince1970] 计算得到。
 *
 * 计算方式：
 * ```kotlin
 * (NSDate.now().timeIntervalSince1970() * 1000).toLong()
 * ```
 *
 * @see NSDateTimestamp
 */
@OptIn(ExperimentalAPI::class)
internal actual fun nowInternal(): Timestamp =
    NSDate.now().toTimestamp()

// NSDate

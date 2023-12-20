package love.forte.simbot.common.time

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval


/**
 * 得到一个记录了当前时间戳信息的 [Timestamp] 实例。
 */
@OptIn(ExperimentalForeignApi::class)
internal actual fun nowInternal(): Timestamp = MillisecondsTimestamp(nowMillis)


@ExperimentalForeignApi
private inline val nowMillis: Long
    get() = memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        (timeVal.tv_sec * 1_000L) + (timeVal.tv_usec / 1_000L)
    }

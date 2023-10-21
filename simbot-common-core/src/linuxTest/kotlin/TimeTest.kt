import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.time
import platform.posix.timeval
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class TimeTest {

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun nowTest() {
        // time
        val nowUnixtime = time(null)
        println(nowUnixtime)
    }


    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun nowTest2() {
        val time = memScoped {
            val timeVal = alloc<timeval>()
            gettimeofday(timeVal.ptr, null)
            timeVal.tv_sec * 1_000L + timeVal.tv_usec / 1_000L
        }

        println(time)
    }
}

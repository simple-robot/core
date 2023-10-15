import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlin.test.Test
import kotlinx.cinterop.*
import platform.posix.*

class TimeTests {
    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun test() {

        // int
//        val  time = memScoped {
//            val timeVal = alloc<timeval>()
////        gettimeofday(timeVal.ptr, null)
//            mingw_gettimeofday(timeVal.ptr, null)
//            (timeVal.tv_sec * 1000) + (timeVal.tv_usec / 1000)
//        }

        val time = time(null)

        println(time)
        println()
    }



    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun nowTest2() {
        val time = memScoped {
            val timeVal = alloc<timeval>()
            mingw_gettimeofday(timeVal.ptr, null)
            timeVal.tv_sec * 1_000L + timeVal.tv_usec / 1_000L
        }

        println(time)
    }
}

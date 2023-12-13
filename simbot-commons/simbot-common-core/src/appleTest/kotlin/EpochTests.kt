import platform.Foundation.NSDate
import platform.Foundation.now
import platform.Foundation.timeIntervalSince1970
import kotlin.test.Test
import kotlin.test.assertEquals

class EpochTests {


    @Test
    fun nowTest() {
        //NSDate ?
        val now = NSDate.now()
        val millis = (now.timeIntervalSince1970() * 1000).toLong()
        println(millis)

        assertEquals(millis.toString().length, 13)
    }
}

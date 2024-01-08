package love.forte.simbot.spring.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test


/**
 *
 * @author ForteScarlet
 */
class ScopeLaunchTests {

    @Test
    fun scopeLaunchAndThrowTest() = runTest {
        try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    repeat(20) { i ->
                        launch {
                            delay(100)
                            error("ERROR in $i")
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}

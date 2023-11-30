import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class DeleteOptionTests {

    @Test
    fun optionAnalysisTest() {
        val options = arrayOf<DeleteOption>(
            StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET,
            StandardDeleteOption.IGNORE_ON_UNSUPPORTED
        )

        val standardAnalysis = options.standardAnalysis()

        assertTrue(StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET in standardAnalysis)
        assertTrue(StandardDeleteOption.IGNORE_ON_UNSUPPORTED in standardAnalysis)
        assertFalse(StandardDeleteOption.IGNORE_ON_FAILURE in standardAnalysis)
    }

    @Test
    fun optionAnalysisTest_NoOptions() {
        val options = arrayOf<DeleteOption>()

        val standardAnalysis = options.standardAnalysis()

        assertFalse(StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET in standardAnalysis)
        assertFalse(StandardDeleteOption.IGNORE_ON_UNSUPPORTED in standardAnalysis)
        assertFalse(StandardDeleteOption.IGNORE_ON_FAILURE in standardAnalysis)
    }

    @Test
    fun optionAnalysisTest_FullOptions() {
        val options: Array<out DeleteOption> = StandardDeleteOption.entries.toTypedArray()

        val standardAnalysis = options.standardAnalysis()

        StandardDeleteOption.entries.forEach {
            assertTrue(it in standardAnalysis)
        }
    }

}

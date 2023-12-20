package love.forte.simbot

import kotlinx.coroutines.Job
import love.forte.simbot.common.coroutines.linkTo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JobLinkTests {

    @Test
    fun childJobLinkToParentAndParentCancelTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        parentJob.cancel()
        assertFalse(childJob.isActive)
    }
    @Test
    fun childJobLinkToParentAndParentCancelButChildUnlinkedTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob).apply { dispose() } }
        parentJob.cancel()
        assertTrue(childJob.isActive)
    }

    @Test
    fun childJobLinkToParentAndChildCancelTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        childJob.cancel()
        assertTrue(parentJob.isActive)
    }

}

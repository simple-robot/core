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

    @Test
    fun childJobLinkToParentAndParentCompleteTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        parentJob.complete()
        assertFalse(childJob.isActive)
    }
    @Test
    fun childJobLinkToParentAndParentCompleteButChildUnlinkedTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob).apply { dispose() } }
        parentJob.complete()
        assertTrue(childJob.isActive)
    }

    @Test
    fun childJobLinkToParentAndChildCompleteTest() {
        val parentJob = Job()
        val childJob = Job().apply { linkTo(parentJob) }
        childJob.complete()
        assertTrue(parentJob.isActive)
    }
}

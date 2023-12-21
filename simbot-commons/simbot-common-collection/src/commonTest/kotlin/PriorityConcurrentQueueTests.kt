import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.collection.createPriorityConcurrentQueue
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class PriorityConcurrentQueueTests {

    @Test
    fun priorityConcurrentQueueIteratorTest_3_priority() {
        val queue = createPriorityConcurrentQueue<String>()

        val priorities = listOf(5, 10, 1)

        priorities.forEach { priority ->
            for (c in 'A'..'C') {
                queue.add(priority, "$priority-$c")
            }
        }

        val joinStr = queue.joinToString(", ")
        assertEquals("1-A, 1-B, 1-C, 5-A, 5-B, 5-C, 10-A, 10-B, 10-C", joinStr)

        queue.add(0, "0-A")

        val joinStr2 = queue.joinToString(", ")
        assertEquals("0-A, 1-A, 1-B, 1-C, 5-A, 5-B, 5-C, 10-A, 10-B, 10-C", joinStr2)
    }

    @Test
    fun priorityConcurrentQueueIteratorTest_remove_in_async() = runTest {
        val queue = createPriorityConcurrentQueue<String>()

        val removeJob = launch {
            while (true) {
                queue.removeIf(1) { it == "1-VALUE" }
                delay(Random.nextLong(10L, 50L))
            }
        }

        val addJobs = mutableListOf<Job>()

        repeat(100) {
            val addJob = launch {
                val priority = Random.nextInt(1, 3)
                queue.add(priority, "$priority-VALUE")
            }
            addJobs.add(addJob)
        }


        addJobs.joinAll()
        delay(100L)
        removeJob.cancel()

        queue.forEach { value ->
            assertFalse { value == "1-VALUE" }
        }

    }


    @Test
    fun priorityConcurrentQueueIteratorTest_iterator_next() {
        val queue = createPriorityConcurrentQueue<String>()
        val iterator = queue.iterator()
        assertFalse(iterator.hasNext())
        queue.add(1, "A")
        assertFalse(iterator.hasNext())
        val newIterator = queue.iterator()
        assertTrue(newIterator.hasNext())
        newIterator.next()
        assertFalse(newIterator.hasNext())
    }

}

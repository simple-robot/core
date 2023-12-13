package love.forte.simbot.core.event

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap

/**
 * 创建优先级并发队列的函数。
 *
 * 调用此函数会返回一个优先级并发队列的实例。
 * 实例的具体实现基于 [ConcurrentSkipListMap] 和 [ConcurrentLinkedQueue]。
 *
 * @param T 队列元素的类型。
 * @return 返回 SkipListPriorityConcurrentQueue 的实例。
 */
public actual fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T> =
    SkipListPriorityConcurrentQueue()


private class SkipListPriorityConcurrentQueue<T> : PriorityConcurrentQueue<T> {
    private val queueMap = ConcurrentSkipListMap<Int, ConcurrentLinkedQueue<T>>()

    override fun add(priority: Int, value: T) {
        val queue = queueMap.computeIfAbsent(priority) { ConcurrentLinkedQueue() }
        queue.add(value)
    }

    override fun remove(priority: Int, target: T) {
        queueMap.compute(priority) { _, q ->
            if (q != null) {
                q.remove(target)
                q.takeIf { it.isNotEmpty() }
            } else {
                null
            }
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        queueMap.compute(priority) { _, q ->
            if (q != null) {
                q.removeIf(predicate)
                q.takeIf { it.isNotEmpty() }
            } else {
                null
            }
        }
    }

    override fun remove(target: T) {
        for (entry in queueMap) {
            val (priority, queue) = entry
            if (queue.remove(target)) {
                queueMap.compute(priority) { _, queue0 ->
                    queue0?.takeIf { it.isNotEmpty() }
                }
                break
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        for (entry in queueMap) {
            val (priority, queue) = entry
            if (queue.removeIf(predicate)) {
                queueMap.compute(priority) { _, queue0 ->
                    queue0?.takeIf { it.isNotEmpty() }
                }
            }
        }
    }

    override fun iterator(): Iterator<T> {
        return Iter()
    }

    private inner class Iter : Iterator<T> {
        private val entries = queueMap.entries.iterator()

        @Volatile
        private var currentIter: Iterator<T>? = nextIter()

        private fun nextIter(): Iterator<T>? {
            return entries.takeIf { it.hasNext() }?.next()?.value?.iterator()
        }

        override fun hasNext(): Boolean {
            var ci = currentIter
            while (ci != null && !ci.hasNext()) {
                ci = nextIter()
                currentIter = ci
            }

            return ci != null
        }

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            return currentIter!!.next()
        }

    }

}

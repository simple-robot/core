package love.forte.simbot.core.event

/**
 * 构建一个基于普通的 [MutableList] 实现的 [PriorityConcurrentQueue]。
 */
public actual fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T> =
    SimpleListPriorityConcurrentQueue()

private class SimpleListPriorityConcurrentQueue<T> : PriorityConcurrentQueue<T> {
    private data class ListWithPriority<T>(val priority: Int, val list: MutableList<T>)

    private val priorities = mutableSetOf<Int>()
    private val lists = mutableListOf<ListWithPriority<T>>()

    private fun findByPriority(priority: Int): ListWithPriority<T>? = lists.find { it.priority == priority }

    override fun add(priority: Int, value: T) {
        if (priority in priorities) {
            findByPriority(priority)?.list?.add(value)
        } else {
            lists.add(ListWithPriority(priority, mutableListOf(value)))
            priorities.add(priority)
            lists.sortBy { it.priority }
        }
    }

    override fun remove(priority: Int, target: T) {
        if (priority in priorities) {
            val found = findByPriority(priority) ?: return
            found.list.remove(target)

            if (found.list.isEmpty()) {
                lists.remove(found)
            }
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        if (priority in priorities) {
            val found = findByPriority(priority) ?: return
            val iter = found.list.iterator()
            while (iter.hasNext()) {
                val v = iter.next()
                if (predicate(v)) {
                    iter.remove()
                    break
                }
            }

            if (found.list.isEmpty()) {
                lists.remove(found)
            }
        }
    }

    override fun removeAllIf(priority: Int, predicate: (T) -> Boolean) {
        if (priority in priorities) {
            val found = findByPriority(priority) ?: return
            found.list.removeAll(predicate)
            if (found.list.isEmpty()) {
                lists.remove(found)
            }
        }
    }

    override fun iterator(): Iterator<T> {
        return lists.asSequence().flatMap { it.list }.iterator()
    }
}

package love.forte.simbot.common.collection


internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private val list = mutableListOf<T>()

    override fun add(value: T) {
        list.add(value)
    }

    override fun remove(value: T) {
        list.remove(value)
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        list.removeAll(predicate)
    }

    override fun iterator(): Iterator<T> = list.iterator()

    override fun toString(): String = list.toString()
}


internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val lists = mutableMapOf<Int, MutableList<T>>()

    override fun add(priority: Int, value: T) {
        val list = lists.getOrPut(priority) { mutableListOf() }
        list.add(value)
    }

    override fun remove(priority: Int, target: T) {
        val list = lists[priority] ?: return
        if (list.removedAndEmpty(target)) {
            // removed and empty, remove list
            lists.remove(priority)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val list = lists[priority] ?: return
        if (list.removedAllAndEmpty(predicate)) {
            // removed and empty, remove list
            lists.remove(priority)
        }
    }

    override fun remove(target: T) {
        with(lists.values.iterator()) {
            while (hasNext()) {
                val value = next()
                if (value.remove(target)) {
                    if (value.isEmpty()) {
                        remove()
                    }
                    break
                }
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        lists.values.removeAll { list -> list.removedAllAndEmpty(predicate) }
    }

    private fun <T> MutableList<T>.removedAndEmpty(target: T): Boolean = remove(target) && isEmpty()
    private fun <T> MutableList<T>.removedAllAndEmpty(predicate: (T) -> Boolean): Boolean =
        removeAll(predicate) && isEmpty()

    override fun iterator(): Iterator<T> {
        return lists.values.asSequence().flatMap { it }.iterator()
    }

    override fun toString(): String = lists.toString()
}

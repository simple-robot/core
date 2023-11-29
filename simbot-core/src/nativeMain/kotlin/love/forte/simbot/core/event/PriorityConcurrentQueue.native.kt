package love.forte.simbot.core.event

import kotlin.concurrent.AtomicReference

/**
 * 构建一个内部基于 [AtomicReference] 的 [PriorityConcurrentQueue] 实现。
 *
 * 此实现内会在每次新增或删除时使用新列表原子（`atomic`）地覆盖原列表，
 * 不会影响到已经被取用的 [iterator][PriorityConcurrentQueue.iterator]。
 *
 */
public actual fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T> =
    AtomicCopyOnWritePriorityConcurrentQueue()

private class AtomicCopyOnWritePriorityConcurrentQueue<T> : PriorityConcurrentQueue<T> {
    private data class ListWithPriority<T>(
        val priority: Int,
        val list: AtomicReference<List<T>>
    )

    private val lists = AtomicReference<List<ListWithPriority<T>>>(emptyList())

    private fun findByPriority(priority: Int): ListWithPriority<T>? =
        lists.value.find { it.priority == priority }

    override fun add(priority: Int, value: T) {
        do {
            val found = findByPriority(priority)

            val done = if (found != null) {
                val foundList = found.list
                val expected = foundList.value
                val newValue = expected + value
                foundList.compareAndSet(expected, newValue);
            } else {
                val listValue = lists.value
                val addedNewValue = lists.compareAndSet(listValue, buildList {
                    addAll(listValue)
                    add(ListWithPriority(priority = priority, list = AtomicReference(listOf(value))))
                    sortBy { it.priority }
                })

                addedNewValue
            }

        } while (done)


    }


    override fun remove(priority: Int, target: T) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = list - target
                val done = updateListForRemoveElement(found, list, newList)
            } while (done)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = ArrayList<T>(list.size)
                var removed = false
                list.filterTo(newList) { v ->
                    if (!removed && predicate(v)) {
                        removed = true; false
                    } else true
                }

                val done = updateListForRemoveElement(found, list, newList)
            } while (done)
        }
    }

    override fun removeAllIf(priority: Int, predicate: (T) -> Boolean) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = list.filterNot(predicate)

                val done = updateListForRemoveElement(found, list, newList)
            } while (done)
        }
    }

    private fun updateListForRemoveElement(
        target: ListWithPriority<T>,
        expectList: List<T>,
        newList: List<T>
    ): Boolean {
        return when {
            expectList.isEmpty() || newList.isEmpty() -> {
                // try to remove found
                val listsValue = lists.value
                lists.compareAndSet(listsValue, listsValue - target)
            }

            expectList.size == newList.size -> {
                // nothing updated.
                true
            }

            else -> {
                // remove element
                target.list.compareAndSet(expectList, newList)
            }
        }

    }

    override fun iterator(): Iterator<T> {
        return lists.value.asSequence().flatMap { it.list.value }.iterator()
    }


}


private inline fun <T> AtomicReference<T>.compareAndSet(block: (T) -> T) {
    do {
        val old = value
        val new = block(old)
        val set = compareAndSet(old, new)
    } while (set)
}

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
            } while (!updateListForRemoveElement(found, list, newList).value)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = list.filterNot(predicate)
            } while (!updateListForRemoveElement(found, list, newList).value)
        }
    }

    override fun remove(target: T) {
        head@while (true) {
            for (listWithPriority in lists.value) {
                while (true) {
                    val list = listWithPriority.list.value
                    val newList = list - target

                    when (val result = updateListForRemoveElement(listWithPriority, list, newList)) {
                        is ElementRemoveResult.RemoveTargetList -> {
                            // 如果企图直接更新 list 且更新成功（移除了当前的 listWithPriority），
                            // 则说明当前 lists 已经发生了改变，重新遍历
                            if (result.value) {
                                continue@head
                            }

                            // 想要删除 listWithPriority，但是删除失败，重新尝试
                            // Just do nothing.
                        }

                        // 没有 target 元素
                        is ElementRemoveResult.SameSize -> break

                        // 删除列表元素
                        is ElementRemoveResult.RemoveElement -> {
                            // 删除成功
                            if (result.value) {
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        head@while (true) {
            for (listWithPriority in lists.value) {
                while (true) {
                    val list = listWithPriority.list.value
                    val newList = list.filterNot(predicate)

                    when (val result = updateListForRemoveElement(listWithPriority, list, newList)) {
                        is ElementRemoveResult.RemoveTargetList -> {
                            // 如果企图直接更新 list 且更新成功（移除了当前的 listWithPriority），
                            // 则说明当前 lists 已经发生了改变，重新遍历
                            if (result.value) {
                                continue@head
                            }

                            // 想要删除 listWithPriority，但是删除失败，重新尝试
                            // Just do nothing.
                        }

                        // 没有 target 元素
                        is ElementRemoveResult.SameSize -> break

                        // 删除列表元素
                        is ElementRemoveResult.RemoveElement -> {
                            // 删除成功，跳出当前 listWithPriority，进入下一个 listWithPriority
                            if (result.value) {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return Is done
     */
    private fun updateListForRemoveElement(
        target: ListWithPriority<T>,
        expectList: List<T>,
        newList: List<T>
    ): ElementRemoveResult {
        return when {
            expectList.isEmpty() || newList.isEmpty() -> {
                // try to remove found
                val listsValue = lists.value
                val result = lists.compareAndSet(listsValue, listsValue - target)
                return ElementRemoveResult.RemoveTargetList.of(result)
            }

            expectList.size == newList.size -> {
                // nothing updated.
                ElementRemoveResult.SameSize
            }

            else -> {
                // remove element
                val result = target.list.compareAndSet(expectList, newList)
                ElementRemoveResult.RemoveElement.of(result)
            }
        }
    }

    private sealed class ElementRemoveResult {
        abstract val value: Boolean

        /**
         * 由于 expectList 或 newList 为空，所以尝试从 lists 中直接移除 target 时的响应
         */
        class RemoveTargetList private constructor(override val value: Boolean) : ElementRemoveResult() {
            companion object {
                val True = RemoveTargetList(true)
                val False = RemoveTargetList(false)
                fun of(value: Boolean) = if (value) True else False
            }
        }

        /**
         * 当 expectList 与 newList 内容长度相同时返回的恒为 true 的结果
         */
        data object SameSize : ElementRemoveResult() {
            override val value: Boolean
                get() = true
        }


        class RemoveElement private constructor(override val value: Boolean) : ElementRemoveResult() {
            companion object {
                val True = RemoveElement(true)
                val False = RemoveElement(false)
                fun of(value: Boolean) = if (value) True else False
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

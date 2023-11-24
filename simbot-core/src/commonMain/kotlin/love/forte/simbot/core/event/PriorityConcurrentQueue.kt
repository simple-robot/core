package love.forte.simbot.core.event

/**
 * 表示一个基于优先级的并发队列，可以根据元素的优先级添加和删除元素。
 *
 * @author ForteScarlet
 */
public interface PriorityConcurrentQueue<T> : Iterable<T> {

    /**
     * 将具有指定优先级的元素添加到集合中。
     *
     * @param priority 元素的优先级
     * @param value 要添加的元素的值
     */
    public fun add(priority: Int, value: T)

    /**
     * 根据给定的优先级和目标对象，从列表中删除指定的项。
     *
     * @param priority 要删除的项目的优先级。
     * @param target 从列表中删除的目标对象。
     */
    public fun remove(priority: Int, target: T)

    /**
     * 根据优先级和条件从列表中删除元素。
     *
     * @param priority 要删除的元素的优先级。只有优先级高于或等于给定优先级的元素才会被删除。
     * @param predicate 用于确定是否应删除元素的条件。只有满足条件的元素才会被删除。
     */
    public fun removeIf(priority: Int, predicate: (T) -> Boolean)

    /**
     * 如果元素满足给定的条件，就从列表中删除所有元素。
     *
     * @param priority 应删除的元素的优先级
     * @param predicate 一个接受类型为T的元素并返回真如果元素应该被删除，否则返回假的函数
     */
    public fun removeAllIf(priority: Int, predicate: (T) -> Boolean)

    /**
     * 返回用于遍历此对象元素的迭代器。
     *
     * @return 允许遍历此对象元素的迭代器对象。
     */
    override fun iterator(): Iterator<T>
}

/**
 * 创建一个优先级并发队列。
 * 这是一个预期的函数，可以根据实际平台的需求提供不同的实现。
 * 优先级并发队列允许在多个线程中同时添加、读取和删除元素，
 * 并且元素的出队顺序会根据它们的优先级来确定。
 *
 * @return 返回一个新创建的优先级并发队列。
 */
public expect fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T>

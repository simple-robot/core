package love.forte.simbot.common.collection


/**
 * Copy and to immutable collections.
 */
public expect fun <T> Collection<T>.toImmutable(): Collection<T>


/**
 * 创建一个优先级并发队列 [PriorityConcurrentQueue] 。
 *
 * 优先级并发队列允许在多个线程中同时添加、读取和删除元素，
 * 并且元素的出队顺序会根据它们的优先级来确定。
 *
 * @return 返回一个新创建的优先级并发队列。
 */
public expect fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T>

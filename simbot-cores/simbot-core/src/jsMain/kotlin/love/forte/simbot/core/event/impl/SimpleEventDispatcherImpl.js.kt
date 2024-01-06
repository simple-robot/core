package love.forte.simbot.core.event.impl

import love.forte.simbot.common.collection.PriorityConcurrentQueue
import love.forte.simbot.event.EventListenerRegistrationHandle

internal actual fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle =
    CountDownEventListenerRegistrationHandle(priority, queue, target)

private class CountDownEventListenerRegistrationHandle<T>(
    private val priority: Int,
    private val queue: PriorityConcurrentQueue<T>,
    private val target: T
) : EventListenerRegistrationHandle {
    private var disposed = false

    override fun dispose() {
        if (!disposed) {
            queue.remove(priority, target)
            disposed = true
        }
    }
}

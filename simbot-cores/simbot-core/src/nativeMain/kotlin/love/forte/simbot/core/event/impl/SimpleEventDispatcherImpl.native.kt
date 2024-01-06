package love.forte.simbot.core.event.impl

import love.forte.simbot.common.collection.PriorityConcurrentQueue
import love.forte.simbot.event.EventListenerRegistrationHandle
import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

internal actual fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle =
    WeakEventListenerRegistrationHandle(priority, queue, target)


@OptIn(ExperimentalNativeApi::class)
private class WeakEventListenerRegistrationHandle<T : Any>(
    private val priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
) : EventListenerRegistrationHandle {
    @Volatile
    private var queueRef: WeakReference<PriorityConcurrentQueue<T>>? = WeakReference(queue)

    @Volatile
    private var targetRef: WeakReference<T>? = WeakReference(target)

    override fun dispose() {
        val qr = queueRef
        val tr = targetRef
        if (qr == null || tr == null) {
            return
        }

        val queue = qr.value
        val target = tr.value

        if (queue != null && target != null) {
            queue.remove(priority, target)
        }

        // clear all
        queueRef = null
        targetRef = null
    }

}

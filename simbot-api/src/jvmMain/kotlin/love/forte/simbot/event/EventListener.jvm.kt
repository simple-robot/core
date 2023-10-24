@file:JvmName("EventListenerJvm")

package love.forte.simbot.event

import kotlinx.coroutines.future.await
import love.forte.simbot.utils.runInNoScopeBlocking
import org.jetbrains.annotations.Blocking
import java.util.concurrent.CompletionStage

/**
 * 一个事件 [Event] 的异步监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [JAsyncEventListener.toEventListener] 转化为 [EventListener] 类型。
 *
 * @see EventListener
 * @see JAsyncEventListener.toEventListener
 *
 * @author ForteScarlet
 */
public interface JAsyncEventListener {
    /**
     * 通过 [context] 异步处理事件并得到异步响应。
     *
     */
    public fun handle(context: EventContext): CompletionStage<out EventResult>
}

/**
 * 将 [JAsyncEventListener] 转化为 [EventListener]。
 */
public fun JAsyncEventListener.toEventListener(): EventListener = JAsyncEventListenerImpl(this)

private class JAsyncEventListenerImpl(private val jaListener: JAsyncEventListener) : EventListener {
    override suspend fun handle(context: EventContext): EventResult =
        jaListener.handle(context).await()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is JAsyncEventListenerImpl) return false

        return jaListener == other.jaListener
    }

    override fun hashCode(): Int = jaListener.hashCode()

    override fun toString(): String {
        return "JAsyncEventListener($jaListener)"
    }
}

/**
 * 一个事件 [Event] 的阻塞监听器。也可以称之为事件处理器。
 *
 * 是针对JVM平台的兼容类型，可以通过 [JBlockingEventListener.toEventListener] 转化为 [EventListener] 类型。
 *
 * @see EventListener
 * @see JBlockingEventListener.toEventListener
 *
 * @author ForteScarlet
 */
public interface JBlockingEventListener {
    /**
     * 通过 [context] 异步处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @Blocking
    public fun handle(context: EventContext): EventResult
}

/**
 * 将 [JBlockingEventListener] 转化为 [EventListener]。
 *
 * 使用 [runInNoScopeBlocking] 作为内部的阻塞调度器。
 */
public fun JBlockingEventListener.toEventListener(): EventListener = JBlockingEventListenerImpl(this)

private class JBlockingEventListenerImpl(private val jbListener: JBlockingEventListener) : EventListener {
    override suspend fun handle(context: EventContext): EventResult = runInNoScopeBlocking {
        jbListener.handle(context)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is JBlockingEventListenerImpl) return false

        return jbListener == other.jbListener
    }

    override fun hashCode(): Int = jbListener.hashCode()

    override fun toString(): String {
        return "JBlockingEventListener($jbListener)"
    }
}

package love.forte.simbot.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import love.forte.simbot.event.JAsyncEventListener.Companion.toEventListener
import love.forte.simbot.event.JBlockingEventListener.Companion.toEventListener
import love.forte.simbot.utils.runInNoScopeBlocking
import org.jetbrains.annotations.Blocking
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

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
public fun interface JAsyncEventListener {
    /**
     * 通过 [context] 异步处理事件并得到异步响应。
     *
     */
    public fun handle(context: EventContext): CompletionStage<out EventResult>

    public companion object {
        /**
         * Converts the given JAsyncEventListener to a standard EventListener.
         *
         * @param listener The JAsyncEventListener to convert.
         * @return The converted EventListener.
         */
        @JvmStatic
        public fun toListener(listener: JAsyncEventListener): EventListener = listener.toEventListener()


        /**
         * 将 [JAsyncEventListener] 转化为 [EventListener]。
         */
        @JvmStatic
        public fun JAsyncEventListener.toEventListener(): EventListener = JAsyncEventListenerImpl(this)
    }
}

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
public fun interface JBlockingEventListener {
    /**
     * 通过 [context] 异步处理事件并得到响应结果。
     *
     * @throws Exception 任何可能抛出的异常
     */
    @Throws(Exception::class)
    @Blocking
    public fun handle(context: EventContext): EventResult

    public companion object {
        /**
         * Converts a JBlockingEventListener to an EventListener.
         *
         * @param dispatcherContext The coroutine context to be used for dispatching events. Default value is Dispatchers.IO.
         * @param listener The JBlockingEventListener to be converted.
         * @return The converted EventListener.
         */
        @JvmStatic
        @JvmOverloads
        public fun toListener(
            dispatcherContext: CoroutineContext = Dispatchers.IO,
            listener: JBlockingEventListener
        ): EventListener = listener.toEventListener(dispatcherContext)


        /**
         * 将 [JBlockingEventListener] 转化为 [EventListener]。
         *
         * 使用 [runInNoScopeBlocking] 作为内部的阻塞调度器。
         */
        @JvmStatic
        @JvmOverloads
        public fun JBlockingEventListener.toEventListener(dispatcherContext: CoroutineContext = Dispatchers.IO): EventListener =
            JBlockingEventListenerImpl(this, dispatcherContext)
    }
}

private class JBlockingEventListenerImpl(
    private val jbListener: JBlockingEventListener,
    private val dispatcherContext: CoroutineContext
) : EventListener {
    override suspend fun handle(context: EventContext): EventResult {
        return withContext(Dispatchers.IO) {
            jbListener.handle(context)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JBlockingEventListenerImpl) return false

        if (jbListener != other.jbListener) return false
        if (dispatcherContext != other.dispatcherContext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jbListener.hashCode()
        result = 31 * result + dispatcherContext.hashCode()
        return result
    }

    override fun toString(): String {
        return "JBlockingEventListener(dispatcherContext=$dispatcherContext)"
    }
}

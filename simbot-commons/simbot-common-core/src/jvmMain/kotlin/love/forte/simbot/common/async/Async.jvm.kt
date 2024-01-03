package love.forte.simbot.common.async

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.common.function.Action
import java.util.concurrent.CompletableFuture

/**
 * [Async] 在 JVM 平台的具体实现。
 *
 * 为了帮助在 JVM 平台上的异步操作，[Async] 增加了方法 [asFuture] 来获取 [CompletableFuture] 表示的异步操作。
 *
 * @see Deferred
 */
public actual class Async<out T> @PublishedApi internal actual constructor(public actual val deferred: Deferred<T>) {
    /**
     * 操作是否正在进行。
     *
     * @see Deferred.isActive
     * @return 操作正在进行则为 true，否则为 false
     */
    public actual val isActive: Boolean
        get() = deferred.isActive

    /**
     * 操作是否已完成。
     *
     * @see Deferred.isCompleted
     * @return 操作已完成则为 true，否则为 false
     */
    public actual val isCompleted: Boolean
        get() = deferred.isCompleted

    /**
     * 操作是否已被取消。
     *
     * @see Deferred.isCancelled
     * @return 操作已被取消则为 true，否则为 false
     */
    public actual val isCancelled: Boolean
        get() = deferred.isCompleted

    /**
     * 当发生错误时的处理函数。
     *
     * [handler] 不会处理类型为 [CancellationException] 的异常。
     * 如果需要处理 [CancellationException]，请参考使用 [onCancellation] 或 [handle]。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun onError(handler: Action<Throwable>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e is Throwable && e !is CancellationException) {
                handler.invoke(e)
            }
        }
    }

    /**
     * 当取消执行时的处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun onCancellation(handler: Action<CancellationException>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e is CancellationException) {
                handler.invoke(e)
            }
        }
    }

    /**
     * 当完成执行时的处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    @ExperimentalCoroutinesApi
    public actual fun onCompletion(handler: Action<T>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e == null) {
                handler.invoke(deferred.getCompleted())
            }
        }
    }

    /**
     * 设置异步处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun handle(handler: Action<Throwable?>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            handler.invoke(e)
        }
    }

    /**
     * 取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public actual fun cancel() {
        deferred.cancel()
    }

    /**
     * 以给定的原因取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public actual fun cancelBy(cause: CancellationException?) {
        deferred.cancel(cause)
    }

    /**
     * 转换 [Async] 对象为 [CompletableFuture]。
     *
     * 使用此方法可以更方便地在 JVM 平台上处理异步操作，尤其是那些需要使用 CompletableFuture 的场景。
     *
     * @return 表示同一异步操作的 [CompletableFuture] 对象
     * @see Deferred.asCompletableFuture
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun asFuture(): CompletableFuture<out T> = deferred.asCompletableFuture()

}

@file:JvmName("EventInterceptors")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

/**
 * 以异步的方式实现 [EventInterceptor].
 *
 * Java 中可以直接通过 `EventInterceptors.async` 构建。
 *
 * @see EventInterceptor
 */
public fun interface JAsyncEventInterceptor : EventInterceptor {
    /**
     * 供于 [JAsyncEventInterceptor] 中异步拦截API的阻塞 Context 代理类型。
     * 如果 [scope] 为 `null`，则异步调度的作用域会使用 [source]。
     */
    public data class Context(
        public val source: EventInterceptor.Context,
        public val scope: CoroutineScope,
    ) {
        public fun invoke(): CompletableFuture<EventResult> =
            scope.future { source.invoke() }

        public fun invoke(eventListenerContext: EventListenerContext): CompletableFuture<EventResult> =
            scope.future { source.invoke(eventListenerContext) }
    }

    /**
     * 构建 [Context]。可以实现此函数来重定义实现逻辑，
     * 例如变更默认的异步执行作用域。
     */
    public fun createContext(context: EventInterceptor.Context): Context =
        Context(context, context.eventListenerContext.context)

    /**
     * 以异步的形式实现 [EventInterceptor.intercept].
     *
     * @see EventInterceptor.intercept
     */
    @Throws(Exception::class)
    public fun intercept(context: Context): CompletableFuture<EventResult>

    @JvmSynthetic
    override suspend fun intercept(context: EventInterceptor.Context): EventResult =
        intercept(createContext(context)).await()
}

/**
 * 构建一个 [JAsyncEventInterceptor].
 *
 * 用于在 Java 中提供给参数为 [EventInterceptor] 的函数。例如：
 *
 * ```java
 * public void run(EventInterceptor interceptor) {
 *     // ...
 * }
 *
 * public void run() {
 *     run(EventInterceptors.async(context -> {
 *         // ...
 *         return CompletableFuture.completedFuture(EventResult.empty());
 *     }));
 * }
 * ```
 *
 * 而避免出现编译错误，也避免需要额外定义变量来接收 Lambda。
 * （因为 [EventInterceptor] 也是函数接口，但是单一函数被隐藏了。）
 *
 */
public fun async(function: JAsyncEventInterceptor): JAsyncEventInterceptor = function


/**
 * 以阻塞的方式实现 [EventInterceptor]
 *
 * @see EventInterceptor
 */
public fun interface JBlockEventInterceptor : EventInterceptor {
    /**
     * 供于 [JBlockEventInterceptor] 中阻塞拦截API的阻塞 Context 代理类型。
     */
    public data class Context(public val source: EventInterceptor.Context) {
        public fun invoke(): EventResult = runInNoScopeBlocking { source.invoke() }

        public fun invoke(eventListenerContext: EventListenerContext): EventResult =
            runInNoScopeBlocking { source.invoke(eventListenerContext) }
    }

    /**
     * 以阻塞的形式实现 [EventInterceptor.intercept].
     *
     * Java 中可以直接通过 `EventInterceptors.block` 构建。
     *
     * @see EventInterceptor.intercept
     */
    @Throws(Exception::class)
    public fun intercept(context: Context): EventResult

    @JvmSynthetic
    override suspend fun intercept(context: EventInterceptor.Context): EventResult =
        intercept(Context(context))
}

/**
 * 构建一个 [JBlockEventInterceptor].
 *
 * 用于在 Java 中提供给参数为 [EventInterceptor] 的函数。例如：
 *
 * ```java
 * public void run(EventInterceptor interceptor) {
 *     // ...
 * }
 *
 * public void run() {
 *     run(EventInterceptors.block(context -> {
 *         // ...
 *         return EventResult.empty();
 *     }));
 * }
 * ```
 *
 * 而避免出现编译错误，也避免需要额外定义变量来接收 Lambda。
 * （因为 [EventInterceptor] 也是函数接口，但是单一函数被隐藏了。）
 *
 */
public fun block(function: JBlockEventInterceptor): JBlockEventInterceptor = function

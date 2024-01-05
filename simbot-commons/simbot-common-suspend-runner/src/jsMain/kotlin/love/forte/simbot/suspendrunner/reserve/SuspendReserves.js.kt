package love.forte.simbot.suspendrunner.reserve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

/**
 * 得到一个将结果转化为 [Promise] 的异步执行转化器。
 */
@Suppress("UNCHECKED_CAST")
public fun <T> promise(): SuspendReserve.Transformer<T, Promise<T>> =
    AsyncTransformer as SuspendReserve.Transformer<T, Promise<T>>

/**
 * 得到一个将结果转化为 [Promise] 的异步执行转化器。
 */
@Suppress("UNCHECKED_CAST")
@JsName("toPromise")
public fun <T> SuspendReserve<T>.promise(): Promise<T> =
    transform(AsyncTransformer as SuspendReserve.Transformer<T, Promise<T>>)


private object AsyncTransformer : SuspendReserve.Transformer<Any?, Promise<*>> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): Promise<*> = scope.promise(context) { block() }
}

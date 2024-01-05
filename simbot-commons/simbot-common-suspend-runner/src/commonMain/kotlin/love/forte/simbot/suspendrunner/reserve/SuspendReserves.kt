@file:JvmName("SuspendReserves")
@file:JvmMultifileClass

package love.forte.simbot.suspendrunner.reserve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 得到一个将结果转化为 [Deferred] 的转化器。
 *
 * _但是实际上在 Kotlin 中直接使用 `scope.async { ... }` 是更好的选择..._
 */
@Suppress("UNCHECKED_CAST")
public fun <T> deferred(): SuspendReserve.Transformer<T, Deferred<T>> =
    DeferredTransformer as SuspendReserve.Transformer<T, Deferred<T>>


private object DeferredTransformer : SuspendReserve.Transformer<Any?, Deferred<*>> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): Deferred<*> = scope.async(context) { block() }
}

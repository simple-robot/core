package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

/**
 * 收集 [StandardEventResult.CollectableReactivelyResult.content] 的结果并返回。
 * 如果结果不可收集或不支持收集，则得到原值。
 *
 * native 平台下支持 Kotlin Coroutines 本身的可挂起类型 [Deferred] 和 [Flow]。
 * 可收集类型参考 [StandardEventResult.CollectableReactivelyResult.content] 说明。
 *
 * @see StandardEventResult.CollectableReactivelyResult.content
 * @return The collected result.
 */
public actual suspend fun StandardEventResult.CollectableReactivelyResult.collectCollectableReactively(): Any? {
    return when (val c = content) {
        null -> null
        is Deferred<*> -> c.await()
        is Flow<*> -> c.toList()
        else -> content
    }
}

@file:JvmMultifileClass
@file:JvmName("EventProcessors")

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.asFlux
import reactor.core.publisher.Flux
import java.util.concurrent.CompletableFuture

/**
 * 推送事件并将结果转化为 [Flux].
 * 需要项目环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 */
public fun EventProcessor.pushAndAsFlux(event: Event): Flux<EventResult> =
    push(event).asFlux()

/**
 * 推送事件并将结果收集为 [C] 后返回 [CompletableFuture].
 */
public fun <C : MutableCollection<in EventResult>> EventProcessor.pushAndCollectToAsync(
    event: Event,
    scope: CoroutineScope,
    collection: C
): CompletableFuture<C> =
    scope.future { push(event).toCollection(collection) }

/**
 * 推送事件并将结果收集为 [List] 后返回 [CompletableFuture].
 */
public fun EventProcessor.pushAndCollectToListAsync(
    event: Event,
    scope: CoroutineScope
): CompletableFuture<out List<EventResult>> = pushAndCollectToAsync(event, scope, ArrayList())

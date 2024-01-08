@file:JvmMultifileClass @file:JvmName("EventProcessors")

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 事件流程处理器。推送一个事件 [Event] 并得到结果。
 * [EventProcessor] 在功能上可以认为是一组 [EventListener] 的统一处理单位。
 *
 * ### 协程上下文
 *
 * 当通过 [EventProcessor.push] 推送一个事件并得到一个事件处理链时，
 * 这其中的每一个事件处理器所处上下文可由 [EventDispatcherConfiguration.coroutineContext]
 * 中的配置值或事件处理链 [Flow] 的结果来决定。
 *
 * ```kotlin
 * val app = launchApplication(...) {
 *      eventDispatcher {
 *          coroutineContext = context1 // 配置事件调度器的统一上下文
 *      }
 * }
 *
 * app.eventDispatcher.register { context ->
 *     withContext(context2) { // 在事件处理逻辑内切换上下文
 *          // ...
 *     }
 *     EventResult.empty()
 * }
 *
 * val flow = app.eventDispatcher.push(event)
 *     .onEach { ... } // 会切换到 context3 上
 *     .flowOn(context3) // 将上游调度器切换至 context3
 *     .onEach { ... } // 会在 collect 所在的默认（当前）环境中
 *     .collect { ... } // 在默认（当前）环境收集结果
 * ```
 *
 * 参考上述示例，协程上下文的使用“优先级”可近似地参考为 `context2` > `context1` > `context3` 。
 *
 * ## Java API
 *
 * 在 `EventProcessors` 的静态API中提供了一些非挂起的可供 Java 友好调用的API，
 * 例如:
 *
 * ```java
 * List<EventResult> resultList = EventProcessors.pushAndCollectToListAsync(processor, event, scope);
 * // ...
 * ```
 *
 * ```java
 * Flux<EventResult> resultList = EventProcessors.pushAndAsFlux(processor, event, scope);
 * // ...
 * ```
 *
 * 其中提供了一些异步或响应式相关的转化、处理API。
 * 对于它们各自的说明、限制、要求则在 [push] 的基础上参考它们的文档说明。
 *
 * @author ForteScarlet
 */
public interface EventProcessor {
    /**
     * 推送一个事件，
     * 得到内部所有事件依次将其处理后得到最终的结果流。
     *
     * 结果流是 _冷流_ 。
     *
     * 只有当对结果进行收集时事件才会真正的被处理。
     * 可以通过响应的流对事件处理量进行控制。
     *
     * 事件内部实际的调度器由构造 [EventProcessor] 时的配置属性和具体实现为准。
     *
     * 返回结果的 [Flow] 中每一次事件调度都可能伴随着上下文的切换（通过 [EventDispatcherConfiguration.coroutineContext] 的配置），
     * 但并非通过 [Flow.flowOn] 进行切换，不会导致实际执行的事件调度逻辑比收集到的逻辑更多。
     *
     * ```kotlin
     * eventDispatcher.push(event)
     *   .take(3)
     *   .collect { ... } // flow 中只会执行优先级最高的三个 listener 并收集到它们的结果
     *
     * eventDispatcher.push(event)
     *      .flowOn(Dispatchers.IO) // 切换事件调度流程中的上下文
     *      .take(3)
     *      .collect { ... } // flow 中切换了调度上下文，这可能会使所有的listener都被实际上的执行，但是只收集到3个最新的结果。
     * ```
     *
     * ### 异常
     *
     * 如果事件处理器 [EventListener] 或者事件拦截器 [EventInterceptor] 的执行过程中产生了异常，
     * 则对应位置的 [EventResult] 将会是包装了此异常的 [StandardEventResult.Error]，
     * 且不会中断后续其他处理器或拦截器的执行。
     *
     * ```kotlin
     * eventDispatcher.push(event)
     *     .onEach { result ->
     *         if (result is StandardEventResult.Error) {
     *             // 这个 result 代表了一个异常
     *         }
     *     }
     *     .collect { ... }
     * ```
     *
     * 如果事件调度拦截器在执行的过程中产生了异常，则会直接向 [Flow] 中抛出异常。此异常可通过 [Flow.catch] 得到。
     *
     * ```kotlin
     * eventDispatcher.push(event)
     *     .catch {
     *         // 调度拦截器 EventDispatchInterceptor 产生了异常
     *     }
     *     .collect { ... }
     * ```
     *
     * 也可以通过一些预设的扩展API来处理部分特殊的响应结果。(对于特殊的响应结果可参考 [StandardEventResult])
     * 例如 [Flow.filterNotInvalid] 会过滤掉所有表示无效的返回值 [StandardEventResult.Invalid]:
     * ```kotlin
     *  eventDispatcher.push(event)
     *     .filterNotInvalid() // 过滤掉所有 Invalid 类型结果
     *     .collect { ... }
     * ```
     *
     * ## Java API
     *
     * 在 `EventProcessors` 的静态API中提供了一些非挂起的可供 Java 友好调用的API，
     * 例如:
     *
     * ```java
     * List<EventResult> resultList = EventProcessors.pushAndCollectToListAsync(processor, event, scope);
     * // ...
     * ```
     *
     * ```java
     * Flux<EventResult> resultList = EventProcessors.pushAndAsFlux(processor, event, scope);
     * // ...
     * ```
     *
     * 其中提供了一些异步或响应式相关的转化、处理API。
     * 对于它们各自的说明、限制、要求则在 [push] 的基础上参考它们的文档说明。
     *
     * @return [EventResult] 结果。如果具有特殊含义，那么可能是 [StandardEventResult] 中的某类型。
     *
     */
    public fun push(event: Event): Flow<EventResult>


    /**
     * 通过 [scope] 将事件推送并异步处理，不关心事件的结果。
     */
    public fun pushAndLaunch(scope: CoroutineScope, event: Event): Job {
        return push(event).launchIn(scope)
    }
}


/**
 * 将事件推送并收集处理。
 */
@JvmSynthetic
public suspend fun EventProcessor.pushAndCollect(event: Event, collector: FlowCollector<EventResult>? = null) {
    with(push(event)) {
        if (collector != null) collect(collector) else collect()
    }
}


/**
 * Filters out any not [StandardEventResult.Invalid] objects from the flow.
 *
 * @return A new flow containing only not [StandardEventResult.Invalid] objects.
 */
public fun Flow<EventResult>.filterNotInvalid(): Flow<EventResult> = filter { it !is StandardEventResult.Invalid }

/**
 * Returns a flow of [EventResult]s that is composed of the events emitted by the original [Flow]
 * until an event of type [StandardEventResult.Invalid] is encountered.
 *
 * @return a flow of [EventResult]s excluding the events of type `StandardEventResult.Invalid`.
 */
public fun Flow<EventResult>.takeWhileNotInvalid(): Flow<EventResult> = takeWhile { it !is StandardEventResult.Invalid }

/**
 * Filters out any not [StandardEventResult.Empty] objects from the flow.
 *
 * @return A new flow containing only not [StandardEventResult.Empty] objects.
 */
public fun Flow<EventResult>.filterNotEmpty(): Flow<EventResult> = filter { it !is StandardEventResult.Empty }

/**
 * Returns a flow of [EventResult]s that is composed of the events emitted by the original [Flow]
 * until an event of type [StandardEventResult.Empty] is encountered.
 *
 * @return a flow of [EventResult]s excluding the events of type `StandardEventResult.Invalid`.
 */
public fun Flow<EventResult>.takeWhileNotEmpty(): Flow<EventResult> = takeWhile { it !is StandardEventResult.Empty }

/**
 * Filters the [Flow] of [EventResult]s and removes all [StandardEventResult.Error] instances.
 *
 * @return A new [Flow] containing only non-error [EventResult]s.
 */
public fun Flow<EventResult>.filterNotError(): Flow<EventResult> = filter { it !is StandardEventResult.Error }

/**
 * Returns a new [Flow] containing elements from the original flow until the first occurrence of an error event.
 *
 * @return a new flow containing elements until the first error event.
 */
public fun Flow<EventResult>.takeWhileNotError(): Flow<EventResult> = takeWhile { it !is StandardEventResult.Error }

/**
 * Throws an exception if the event result is of type [StandardEventResult.Error].
 * Otherwise, returns the event result unchanged.
 *
 * e.g.
 * ```kotlin
 * flow.throwIfError()
 * .catch { e -> ... } // 抛出后即可在此处捕获到
 * .collect { ... }
 * ```
 *
 * @return A [Flow] that emits event results without errors.
 */
public fun Flow<EventResult>.throwIfError(): Flow<EventResult> =
    map { if (it is StandardEventResult.Error) throw it.content else it }

/**
 * 处理当前流中经过的每一个
 * [StandardEventResult.Error] 类型的结果。
 */
public inline fun Flow<EventResult>.onEachError(crossinline action: (StandardEventResult.Error) -> Unit): Flow<EventResult> =
    onEach { if (it is StandardEventResult.Error) action(it) }

/**
 * Filters the non-empty [EventResult] objects from the given flow.
 *
 * @receiver The flow of [EventResult] objects.
 * @return A new flow containing only the non-empty [EventResult] objects.
 */
public fun Flow<EventResult>.filterNotEmptyResult(): Flow<EventResult> =
    filter { it !is StandardEventResult.EmptyResult }

/**
 * Returns a Flow of [EventResult]s which stops emitting elements when it encounters an empty [EventResult].
 *
 * @return a Flow of [EventResult]s until an empty [EventResult] is encountered.
 */
public fun Flow<EventResult>.takeWhileNotEmptyResult(): Flow<EventResult> =
    takeWhile { it !is StandardEventResult.EmptyResult }

/**
 * 从当前流中过滤出 **非无效** 的类型，即过滤出不是下述类型之一的其他结果：
 * - [StandardEventResult.Error]
 * - [StandardEventResult.EmptyResult]
 *
 */
public fun Flow<EventResult>.filterOnlyValid(): Flow<EventResult> = filterNotEmptyResult().filterNotError()

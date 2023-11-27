@file:JvmMultifileClass
@file:JvmName("EventProcessors")

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


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
     * val flow = eventDispatcher.push(event).take(3)
     * flow.collect { ... } // flow 中只会执行优先级最高的三个 listener 并收集到它们的结果
     *
     * val flow1 = eventDispatcher.push(event)
     *      .flowOn(Dispatchers.IO) // 切换事件调度流程中的上下文
     *      .take(3)
     * flow.collect { ... } // flow 中切换了调度上下文，这可能会使所有的listener都被实际上的执行，但是只收集到3个最新的结果。
     * ```
     *
     */
    public fun push(event: Event): Flow<EventResult>
}

/**
 * 通过 [scope] 将事件推送并异步处理，不关心事件的结果。
 */
public fun EventProcessor.pushAndLaunch(scope: CoroutineScope, event: Event): Job {
    return push(event).launchIn(scope)
}


/**
 * Filters out any invalid [EventResult] objects from the flow.
 *
 * @return A new flow containing only valid [EventResult] objects.
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
 * @return A [Flow] that emits event results without errors.
 */
public fun Flow<EventResult>.throwIfError(): Flow<EventResult> =
    map { if (it is StandardEventResult.Error) throw it.content else it }

/**
 * Filters the non-empty [EventResult] objects from the given flow.
 *
 * @receiver The flow of [EventResult] objects.
 * @return A new flow containing only the non-empty [EventResult] objects.
 */
public fun Flow<EventResult>.filterNotEmpty(): Flow<EventResult> = filter { it !is StandardEventResult.EmptyResult }

/**
 * Returns a Flow of [EventResult]s which stops emitting elements when it encounters an empty [EventResult].
 *
 * @return a Flow of [EventResult]s until an empty [EventResult] is encountered.
 */
public fun Flow<EventResult>.takeWhileNotEmpty(): Flow<EventResult> =
    takeWhile { it !is StandardEventResult.EmptyResult }

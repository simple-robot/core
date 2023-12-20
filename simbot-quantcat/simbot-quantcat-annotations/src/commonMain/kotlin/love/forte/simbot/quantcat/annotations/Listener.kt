package love.forte.simbot.quantcat.annotations

import love.forte.simbot.common.PriorityConstant

/**
 * 标记一个函数为监听函数/事件处理器。
 *
 * 被 [Listener] 标记的函数在进行处理的时候会根据此函数的参数尝试自动分析其监听目标。
 * 一个事件处理器建议只有**一个** [Event][love.forte.simbot.event.Event] 类型的参数。
 *
 * ```kotlin
 * suspend fun listenFoo(event: FooEvent) {
 *     // ...
 * }
 * ```
 *
 * 在 Kotlin 中，被标记的函数最好是可挂起函数（标记 `suspend`）。
 *
 *
 * @property id 此事件处理器的id。通常用于日志输出或调试用。默认会根据函数生成一个ID。
 * @property priority 此事件处理器的优先级
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Listener(
    val id: String = "",
    val priority: Int = PriorityConstant.NORMAL,
)


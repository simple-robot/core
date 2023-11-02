package love.forte.simbot.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import love.forte.simbot.application.Application
import love.forte.simbot.function.ConfigurerFunction
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 事件调度器。
 * [EventDispatcher] 拥有 [EventProcessor] 和 [EventListenerRegistrar] 的职责，
 * 是对事件调度、事件监听函数管理的统一单元。
 *
 * @author ForteScarlet
 */
public interface EventDispatcher : EventProcessor {
    // TODO ..?
}

/**
 * 针对 [EventDispatcher] 在 [EventDispatcherFactory] 中使用的基础配置类信息。
 * 不同的 [EventDispatcher] 可以对 [EventDispatcherConfiguration] 进行扩展。
 *
 * [EventDispatcherConfiguration] 最少也要满足一些所需的配置内容。
 * 最少也要在不支持的情况下给出警告日志或异常。
 *
 */
public open class EventDispatcherConfiguration {

    /**
     * 用于 [EventDispatcher] 中进行事件调度的协程上下文。
     * [coroutineContext] 会作为调度事件时候使用的调度器，
     * 事件在处理的时候会被切换至此上下文中（例如使用其中的调度器等）。
     *
     * 如果 [coroutineContext] 中存在 [Job]，则此 [Job] 会**被剔除**。
     * [EventDispatcher] 的调度结果最终以流 [Flow] 的形式提供，
     * 因此每次调度任务的生命周期由接收者决定，[Job] 不起作用（[Flow] 的调度上下文中也不允许 [Job] 存在）。
     *
     */
    public open var coroutineContext: CoroutineContext = EmptyCoroutineContext
}

/**
 * 用于构建或在 [Application] 构建阶段配置 [EventDispatcher] 的构建工厂。
 */
public interface EventDispatcherFactory<C : EventDispatcherConfiguration, out V : EventDispatcher> {

    /**
     * 用于 [Application] 配置 [EventDispatcherFactory] 时使用的标识类型。
     * 一个 [Application] 只能配置一个类型的 [EventDispatcher],
     * 而判断不同调度器之间区别的方式便是借由 [EventDispatcherFactory.key]。
     *
     * 在 Kotlin 中，[Key] 的实现推荐为一个 `object` 类型
     * （例如 [EventDispatcherFactory] 实现对应的伴生对象）。
     * 在 JVM 或其他实现中，[Key] 的实现至少应保证其实例唯一，
     * 或 [hashCode] 与 [equals] 直接具有正常的关联性。
     *
     */
    public interface Key

    /**
     * 用于 [Application] 配置 [EventDispatcherFactory] 时使用的标识类型。
     * @see Key
     */
    public val key: Key

    /**
     * 提供配置逻辑函数，并得到结果 [V] 。
     *
     * @param configurer 配置类的配置逻辑。
     */
    public fun create(configurer: ConfigurerFunction<C>): V

    /**
     * 使用默认的配置（没有额外配置逻辑）构建并得到结果 [V] 。
     */
    public fun create(): V = create {}
}


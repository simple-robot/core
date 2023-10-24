package love.forte.simbot.event


/**
 * 事件调度器。
 * [EventDispatcher] 拥有 [EventProcessor] 和 [EventListenerRegistrar] 的职责，
 * 是对事件调度、事件监听函数管理的统一单元。
 *
 * @author ForteScarlet
 */
public interface EventDispatcher : EventProcessor {

}

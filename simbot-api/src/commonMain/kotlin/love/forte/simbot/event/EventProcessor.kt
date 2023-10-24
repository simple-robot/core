package love.forte.simbot.event

import kotlinx.coroutines.flow.Flow


/**
 * 事件流程处理器。推送一个事件 [Event] 并得到结果。
 * [EventProcessor] 在功能上可以认为是一组 [EventListener] 的统一处理单位。
 *
 * @author ForteScarlet
 */
public interface EventProcessor {

    /**
     * 推送一个事件，
     * 得到内部所有事件依次将其处理后得到最终的结果流。
     *
     * 只有当对结果进行收集时事件才会真正的被处理。
     * 可以通过响应的流对事件处理量
     *
     */
    public fun push(): Flow<EventResult>

}

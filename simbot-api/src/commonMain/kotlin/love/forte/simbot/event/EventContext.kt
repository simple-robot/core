package love.forte.simbot.event

/**
 * 一个在事件处理流程中流转的上下文。
 * 用于承载本次事件处理前后的诸项信息。
 *
 * @author ForteScarlet
 */
public interface EventContext {
    /**
     * 本次事件处理流程中被处理的事件。
     */
    public val event: Event
}

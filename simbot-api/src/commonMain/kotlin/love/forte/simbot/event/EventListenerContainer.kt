package love.forte.simbot.event


/**
 * 事件监听器的“容器”接口，提供用于获取其中的所有 [EventListener] 的API。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer {

    /**
     * 得到当前容器内所有的 [EventListener] 的序列。
     * 如无特殊说明则会按照优先级顺序获取。
     */
    public val listeners: Sequence<EventListener>


}

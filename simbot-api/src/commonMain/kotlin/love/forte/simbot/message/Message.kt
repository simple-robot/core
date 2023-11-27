package love.forte.simbot.message


/**
 *
 * 一个 **消息**。
 *
 * @see Message.Element
 * @see Messages
 *
 * @author ForteScarlet
 */
public sealed interface Message {

    /**
     * 一个 **消息元素**，是消息链中的最小单位。
     * 消息元素本身也是消息。
     */
    public interface Element : Message
}



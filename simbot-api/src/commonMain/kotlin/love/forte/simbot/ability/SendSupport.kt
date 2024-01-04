package love.forte.simbot.ability

import love.forte.simbot.definition.Actor
import love.forte.simbot.definition.Contact
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.suspendrunner.JST


/**
 * 对消息发送行为的支持。通常由部分 [Actor] 类型实现，例如 [Contact]。
 *
 *  @author ForteScarlet
 */
@JST
public interface SendSupport {
    /**
     * 发送一段纯文本消息。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun send(text: String): MessageReceipt

    /**
     * 发送一个消息 [Message]。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun send(message: Message): MessageReceipt

    /**
     * 使用 [MessageContent] 作为消息发送。
     * 不同的组件可能会根据 [MessageContent] 的具体类型做针对性的优化，
     * 并在不支持的情况下降级为使用 [MessageContent.messages]。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun send(messageContent: MessageContent): MessageReceipt

}

package love.forte.simbot.ability

import love.forte.simbot.JST
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


/**
 *
 * @author ForteScarlet
 */
@JST
public interface SendSupport {
    // TODO


    public suspend fun send(message: Message): MessageReceipt


}

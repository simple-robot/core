package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.common.attribute.AttributeMap
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.EventResult


public interface KFunctionEventListener : EventListener {
    public val attributes: AttributeMap


}





/**
 *
 * @author ForteScarlet
 */
internal class KFunctionEventListenerImpl(
    private val attributeMap: AttributeMap,

) : EventListener {
    // TODO

    override suspend fun handle(context: EventListenerContext): EventResult {
        TODO("Not yet implemented")
    }
}

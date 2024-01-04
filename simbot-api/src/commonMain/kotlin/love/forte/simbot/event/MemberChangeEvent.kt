package love.forte.simbot.event

import love.forte.simbot.definition.Member
import love.forte.simbot.suspendrunner.JSTP


/**
 * 当 [Member] 发生了某种变化时的事件。
 *
 * @author ForteScarlet
 */
@JSTP
public interface MemberChangeEvent : ChangeEvent, MemberEvent {
    /**
     * 发送了变化的 [Member]。
     *
     */
    override suspend fun content(): Member
}

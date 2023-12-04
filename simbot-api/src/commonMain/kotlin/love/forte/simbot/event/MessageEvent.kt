package love.forte.simbot.event

import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.definition.*
import love.forte.simbot.message.MessageContent

/**
 * 一个 [Bot] 收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface MessageEvent : BotEvent, ReplySupport {
    /**
     * 事件中收到的消息内容。
     */
    public val messageContent: MessageContent
}

/**
 * 一个 [Bot] 从 [ChatRoom] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatRoomMessageEvent : MessageEvent, ChatRoomEvent

/**
 * 一个 [Bot] 从 [ChatGroup] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatGroupMessageEvent : ChatRoomMessageEvent, ChatGroupEvent

/**
 * 一个 [Bot] 从 [ChatChannel] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatChannelMessageEvent : ChatRoomMessageEvent, ChatChannelEvent

/**
 * 一个 [Bot] 从 [Member] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface MemberMessageEvent : MessageEvent, MemberEvent

/**
 * 一个 [Bot] 从 [ChatGroup] 中的 [Member] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface ChatGroupMemberMessageEvent : MessageEvent, MemberEvent {
    /**
     * 事件中 [member][content] 所在的 [ChatGroup]。
     */
    override suspend fun organization(): ChatGroup
}

/**
 * 一个 [Bot] 从 [Guild] 中的 [Member] 处收到消息的事件。
 *
 * @author ForteScarlet
 */
public interface GuildMemberMessageEvent : MessageEvent, MemberEvent {
    /**
     * 事件中 [member][content] 所在的 [Guild]。
     */
    override suspend fun organization(): Guild
}

/**
 * 一个 [Bot] 从 [Contact] 处收到消息的事件。
 */
public interface ContactMessageEvent : MessageEvent, ContactEvent

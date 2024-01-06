package love.forte.simbot.event

import love.forte.simbot.definition.*
import love.forte.simbot.suspendrunner.STP


/**
 * 一个以某 [Actor] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ActorEvent : BotEvent {
    /**
     * 被作为事件中心的 [Actor]。
     */
    public suspend fun content(): Actor
}

/**
 * 一个以某 [Contact] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ContactEvent : ActorEvent {
    /**
     * 被作为事件中心的 [Contact]。
     */
    override suspend fun content(): Contact
}

/**
 * 一个以某 [Organization] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface OrganizationEvent : ActorEvent {
    /**
     * 被作为事件中心的 [Organization]。
     */
    override suspend fun content(): Organization
}

/**
 * 一个以某 [ChatRoom] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatRoomEvent : ActorEvent {
    /**
     * 被作为事件中心的 [ChatRoom]。
     */
    override suspend fun content(): ChatRoom
}

/**
 *
 * 一个以某 [ChatGroup] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatGroupEvent : ChatRoomEvent, OrganizationEvent {
    /**
     * 被作为事件中心的 [ChatGroup]。
     */
    override suspend fun content(): ChatGroup
}

/**
 * 一个以某 [Guild] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildEvent : OrganizationEvent {
    /**
     * 被作为事件中心的 [Guild]。
     */
    override suspend fun content(): Guild
}

/**
 * 一个以某 [Channel] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChannelEvent : ActorEvent {

    /**
     * 事件中的 [channel][content] 所属的 [Guild]。
     *
     */
    public suspend fun guild(): Guild

    /**
     * 被作为事件中心的 [Channel]。
     */
    override suspend fun content(): Channel
}

/**
 * 一个以某 [ChatChannel] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatChannelEvent : ChannelEvent, ChatRoomEvent {
    /**
     * 被作为事件中心的 [ChatChannel]。
     */
    override suspend fun content(): ChatChannel
}

/**
 * 一个以某 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface MemberEvent : ActorEvent {
    /**
     * 事件中 [member][content] 所属的 [Organization]。
     *
     */
    public suspend fun organization(): Organization

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}

/**
 * 一个以某 [ChatGroup] 中的 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatGroupMemberEvent : MemberEvent {
    /**
     * 事件中 [member][content] 所属的 [ChatGroup]。
     */
    override suspend fun organization(): ChatGroup

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}

/**
 * 一个以某 [Guild] 中的 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildMemberEvent : MemberEvent {
    /**
     * 事件中 [member][content] 所属的 [Guild]。
     */
    override suspend fun organization(): Guild

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}


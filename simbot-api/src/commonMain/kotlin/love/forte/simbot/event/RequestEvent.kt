package love.forte.simbot.event

import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization


/**
 * [Bot] 收到的某种与请求/申请有关的事件。
 *
 * @author ForteScarlet
 */
public interface RequestEvent : BotEvent {
    /**
     * 伴随请求的附加消息。
     *
     * 在没有、不支持的情况下可能为 `null`。
     */
    public val message: String?

    /**
     * 此申请的主、被动类型。
     * 如果无法界定，则给一个默认值，并提供相关的说明。
     */
    public val type: Type

    /**
     * [RequestEvent] 的主、被动类型。
     *
     */
    public enum class Type {
        /**
         * 主动的。例如是主动发起的申请。
         */
        PROACTIVE,

        /**
         * 被动的。例如是被邀请的。
         */
        PASSIVE;
    }

    /**
     * 拒绝此请求。
     *
     * @throws Exception 任何可能产生的错误。
     */
    @JST
    public suspend fun reject()

    /**
     * 接受此请求。
     *
     * @throws Exception 任何可能产生的错误。
     */
    @JST
    public suspend fun accept()
}


/**
 * [Bot] 收到的某种与 [Organization] 相关的请求/申请有关的事件。
 *
 * 通常情况下，如果 [Bot] 想收到某种与 [Organization] 相关的请求事件，
 * 需要在此组织内拥有一定的权限。
 *
 * @author ForteScarlet
 */
public interface OrganizationRequestEvent : RequestEvent, OrganizationEvent

/**
 * 某个用户想要假如目标 [Organization] 的请求事件。
 *
 * @author ForteScarlet
 */
public interface OrganizationJoinRequestEvent : OrganizationEvent {
    /**
     * 申请者的 ID。
     */
    public val requesterId: ID

    /**
     * 尝试获取申请者的一些基础信息。
     * 如果无法获取或不支持，则可能得到 `null`。
     */
    @JSTP
    public suspend fun requester(): Requester?
}

/**
 * [OrganizationJoinRequestEvent] 中申请者的部分基础信息。
 * [Requester] 中的所有属性都可能在不支持、无法获取等情况下得到 `null`。
 *
 */
public interface Requester {
    /**
     * 申请者的名称。
     */
    public val name: String?

}

/**
 * 某用户申请加入 [ChatGroup] 的事件。
 *
 * @author ForteScarlet
 *
 */
@JSTP
public interface ChatGroupJoinRequestEvent : OrganizationJoinRequestEvent, ChatGroupEvent {
    /**
     * 被申请加入的 [ChatGroup]。
     */
    override suspend fun content(): ChatGroup
}

/**
 * 某用户申请加入 [Guild] 的事件。
 *
 * @author ForteScarlet
 *
 */
@JSTP
public interface GuildJoinRequestEvent : OrganizationJoinRequestEvent, GuildEvent {
    /**
     * 被申请加入的 [Guild]。
     */
    override suspend fun content(): Guild
}

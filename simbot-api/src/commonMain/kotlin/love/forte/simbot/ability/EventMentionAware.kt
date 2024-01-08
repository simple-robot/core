package love.forte.simbot.ability

import love.forte.simbot.common.id.ID
import love.forte.simbot.event.Event
import love.forte.simbot.message.MessageContent


/**
 * 实现 [EventMentionAware] 以对外提供一个基于 [Event] 判断自身是否被提及（或者说被 `at`）的能力。
 * 通常由 [Bot][love.forte.simbot.bot.Bot] 实现，用于进行一些扩展能力的支持。
 *
 * @author ForteScarlet
 */
public interface EventMentionAware {
    /**
     * 根据提供的 [event] 判断此事件中是否提及了自身。
     */
    public fun isMention(event: Event): Boolean
}

/**
 * 实现 [MentionedTargetAware] 以对外提供一个根据指定 [ID]
 * 判断自身中是否包含了对此 id 目标提及的内容。
 * 通常由 [MessageContent] 实现，用于进行一些扩展能力的支持。
 */
public interface MentionedTargetAware {
    /**
     * 根据提供的 [id] 判断自己包含的信息中是否提及了目标。
     */
    public fun isMentioned(id: ID): Boolean
}

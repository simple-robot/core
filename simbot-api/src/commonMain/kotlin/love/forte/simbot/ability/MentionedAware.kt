package love.forte.simbot.ability

import love.forte.simbot.event.Event


/**
 * 实现 [MentionedAware] 以对外提供一个基于 [Event] 判断自身是否被提及（或者说被 `at`）的能力。
 * 通常由 [Bot][love.forte.simbot.bot.Bot] 实现，用于进行一些扩展能力的支持。
 *
 * @author ForteScarlet
 */
public interface MentionedAware {

    /**
     * 根据提供的 [event] 判断此事件中是否提及了自身。
     */
    public fun isMentioned(event: Event): Boolean

}

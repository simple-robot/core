package love.forte.simbot.event

import love.forte.simbot.bot.Bot
import love.forte.simbot.id.ID
import love.forte.simbot.id.IDContainer
import love.forte.simbot.timestamp.Timestamp

/**
 * 一个 **事件**。
 *
 * @author ForteScarlet
 */
public interface Event : IDContainer {
    /**
     * 事件的ID。
     * 如果平台事件中不存在可用ID，则此值可能为一个随机值。
     */
    public override val id: ID

    /**
     * 事件发生的时间或此事件被接收到的时间。
     *
     * 如果原始事件内支持此类时间属性则直接使用，
     * 否则 [time] 有可能是 [Event] 对象被构建或原始事件被接收到的时间。
     */
    public val time: Timestamp
}

/**
 * 与 [Bot] 相关的事件。
 *
 * @author ForteScarlet
 */
public interface BotEvent : Event {
    /**
     * 相关的 [Bot]。
     */
    public val bot: Bot
}

/**
 * 发生了某种变化的事件。
 *
 * @author ForteScarlet
 */
public interface ChangeEvent : Event {
    /**
     * 发生了变化的主体。
     */
    public suspend fun content(): Any?
}

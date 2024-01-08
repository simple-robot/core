package love.forte.simbot.test.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.event.BotEvent
import love.forte.simbot.event.Event

/**
 * 用于测试的 [Event] 基类
 *
 */
public interface BaseTestEvent : Event {
    override val time: Timestamp
        get() = testTimestamp

    public companion object {
        @OptIn(ExperimentalSimbotAPI::class)
        public var testTimestamp: Timestamp = Timestamp.now()
    }
}

/**
 * 用于测试的 [Event] 实现
 */
public open class TestEvent(override val id: ID) : Event, BaseTestEvent

/**
 * 用于测试的 [BotEvent] 实现
 */
public open class TestBotEvent(override val id: ID, override val bot: Bot) : BotEvent,
    BaseTestEvent

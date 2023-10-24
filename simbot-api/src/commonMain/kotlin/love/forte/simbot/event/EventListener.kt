package love.forte.simbot.event

import kotlin.jvm.JvmSynthetic

/**
 * 一个事件 [Event] 的监听器。也可以称之为事件处理器。
 * [EventListener] 是针对一个 [Event] 进行处理的逻辑单元。
 *
 * @author ForteScarlet
 */
public interface EventListener {
    /**
     * 处理事件，并得到一个处理的响应。
     *
     * @throws Exception 任何可能在处理过程中抛出的异常
     */
    @JvmSynthetic
    @Throws(Exception::class)
    public suspend fun handle(context: EventContext): EventResult
}

package love.forte.simbot.quantcat.common.listener

import love.forte.simbot.event.EventListener
import kotlin.reflect.KFunction

/**
 *
 * 基于函数体 [KFunction] 的监听函数执行器。
 *
 * @author ForteScarlet
 */
public abstract class FunctionalEventListener : EventListener {
    protected abstract val caller: KFunction<*>
}

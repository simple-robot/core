package love.forte.simbot.bot

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ability.LifecycleAware

/**
 *
 * @author ForteScarlet
 */
public interface Bot : LifecycleAware, CoroutineScope {
}

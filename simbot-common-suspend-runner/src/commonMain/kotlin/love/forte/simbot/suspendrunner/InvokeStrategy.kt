package love.forte.simbot.suspendrunner

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
public interface InvokeStrategy<T, R> {

    public operator fun <T1 : T> invoke(
        block: suspend () -> T1,
        scope: CoroutineScope? = null,
        context: CoroutineContext? = null
    ): R

}


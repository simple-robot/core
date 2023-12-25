package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.newFixedThreadPoolContext

/**
 * 获取 [Dispatchers.IO] 调度器。
 */
internal actual fun ioDispatcher(): CoroutineDispatcher? = Dispatchers.IO

/**
 * 获取自定义调度器。
 *
 * native 平台下 [maxThreads] 和 [keepAliveMillis] 无效
 *
 */
internal actual fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?,
): CoroutineDispatcher? {
    val core = coreThreads ?: return null
    require(core <= 1) { "'coreThreads' must >= 1, but $core" }

    return newFixedThreadPoolContext(core, name ?: "Custom-DP.FT.$core")
}

/**
 * 得到 `null`。
 */
internal actual fun virtualDispatcher(): CoroutineDispatcher? = null

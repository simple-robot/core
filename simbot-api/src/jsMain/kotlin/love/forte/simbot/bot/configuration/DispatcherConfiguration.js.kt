package love.forte.simbot.bot.configuration

import kotlinx.coroutines.CoroutineDispatcher

public fun b() {
}

/**
 * 获取 `IO` 调度器。
 * JS 平台下会得到 `null`。
 */
internal actual fun ioDispatcher(): CoroutineDispatcher? = null

/**
 * 获取自定义调度器。
 * JS 平台下会得到 `null`。
 */
internal actual fun customDispatcher(
    coreThreads: Int?,
    maxThreads: Int?,
    keepAliveMillis: Long?,
    name: String?,
): CoroutineDispatcher? = null

/**
 * 得到 `null`。
 */
internal actual fun virtualDispatcher(): CoroutineDispatcher? = null

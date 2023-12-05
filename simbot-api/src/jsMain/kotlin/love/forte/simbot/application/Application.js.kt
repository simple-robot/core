package love.forte.simbot.application

import love.forte.simbot.annotations.InternalAPI

/**
 * 提供给 [Components]、[Plugins]、[BotManagers] 实现的平台额外能力的接口。
 *
 * [PlatformCollection] 在 JS 平台下没有额外实现。
 *
 */
@InternalAPI
public actual interface PlatformCollection<out T> : Collection<T>

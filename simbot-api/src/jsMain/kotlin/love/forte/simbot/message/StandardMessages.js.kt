package love.forte.simbot.message

import love.forte.simbot.resource.Resource

/**
 * 将 [Resource] 转化为 [OfflineResourceImage]。
 *
 */
public actual fun Resource.toOfflineResourceImage(): OfflineResourceImage =
    SimpleOfflineResourceImage(this)

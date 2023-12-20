package love.forte.simbot.definition

import love.forte.simbot.JSTP
import love.forte.simbot.common.id.ID


/**
 * 一个分组。分组主要可能存在于频道服务器中，
 * 并用于对各子频道进行分组。
 *
 * 分组只提供用于获取唯一标识 [id] 的属性。因为有些情况下，
 * 一个“分组”的其他属性可能都需要查询，或至少不会伴随着事件被提供。
 *
 * @author ForteScarlet
 */
public interface Category {
    /**
     * 分组的ID。
     */
    public val id: ID

    /**
     * 获取分组的名称。如果分组不存在名称或无法获取则会得到 `null`。
     */
    @JSTP
    public suspend fun name(): String?
}

package love.forte.simbot.event

import love.forte.simbot.id.ID

/**
 * 一个 **事件**。
 *
 * @author ForteScarlet
 */
public interface Event {
    /**
     * 事件的ID。
     * 如果平台事件中不存在可用ID，则此值可能为一个随机值。
     */
    public val id: ID
}

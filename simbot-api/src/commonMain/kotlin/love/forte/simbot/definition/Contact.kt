package love.forte.simbot.definition

import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.ability.SendSupport


/**
 *
 * 一个联系人。
 *
 * 联系人是一种可以与 bot 建立独立会话、进行通讯的行为目标。
 * 联系人可能代表一个其他用户，也可能代表一个与某用户关联的“会话”。
 *
 * ### DeleteSupport
 *
 * 联系人有可能会实现 [DeleteSupport]。如果实现，则或许代表 bot 可以主动的与此联系人断开关系，
 * 或者主动删除与之关联的 “会话”。
 * 具体的实际含义由实现者定义并提供说明。
 *
 *
 * @author ForteScarlet
 */
public interface Contact : Actor, SendSupport {
    /**
     * 此联系人的名称
     */
    public val name: String

    /**
     * 此联系人的头像（如果有的话）。
     *
     */
    public val avatar: String?

}

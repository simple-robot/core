package love.forte.simbot.definition

import love.forte.simbot.common.id.ID


/**
 * 一个 [组织][Organization] 中的角色或权限的描述。
 *
 * @author ForteScarlet
 */
public interface Role {
    /**
     * 这个角色的ID
     */
    public val id: ID

    /**
     * 这个角色的名称。
     */
    public val name: String

    /**
     * 此角色是否拥有 _管理权限_ 。
     *
     * 此处的 _管理权限_ 以其拥有者是否能操作或影响其他用户为标准，
     * 例如能够修改他人昵称等。
     *
     */
    public val isAdmin: Boolean
}

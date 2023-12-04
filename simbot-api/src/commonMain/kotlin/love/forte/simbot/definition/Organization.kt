package love.forte.simbot.definition

import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.collection.Collectable
import love.forte.simbot.id.ID


/**
 * 一个组织。
 *
 * 一个组织是一个拥有多个 [成员][Member] 的行为主体。
 *
 * @see Guild
 * @see ChatGroup
 *
 * @author ForteScarlet
 */
public interface Organization : Actor {
    /**
     * 此组织的名称。
     */
    public val name: String

    /**
     * 此组织的拥有者的ID。
     *
     * 如果不支持获取则可能得到 `null`。
     */
    public val ownerId: ID?

    /**
     * 根据ID寻找或查询指定的成员信息。
     * 如果找不到则会得到 `null`。
     *
     * @throws Exception 可能产生任何异常
     */
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    public suspend fun member(id: ID): Member?

    /**
     * 获取此组织内的所有成员集合。也会包括 bot 自身在组织内的表现。
     */
    public val members: Collectable<Member>

    /**
     * bot 在当前组织内作为 [成员][Member] 的表现。
     *
     * @throws Exception 可能产生任何异常
     */
    @JSTP
    public suspend fun botAsMember(): Member

    /**
     * 此组织中的所有可用角色集。
     * 有可能得到一个空的集合 —— 这说明当前组织没有角色这一概念。
     */
    public val roles: Collectable<Role>
}

/**
 * 一个组织内的成员。
 *
 * @see Organization
 */
public interface Member : Actor, SendSupport {
    /**
     * 此成员的名称。通常是代表它作为一个用户的名称，而不是在某个组织内的“昵称”。
     */
    public val name: String

    /**
     * 此成员在组织内的昵称。如果未设置或无法获取则会得到 `null`。
     */
    public val nick: String?
}

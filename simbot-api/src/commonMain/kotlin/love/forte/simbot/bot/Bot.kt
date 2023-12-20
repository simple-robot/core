package love.forte.simbot.bot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IDContainer
import love.forte.simbot.component.Component
import love.forte.simbot.definition.Channel
import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.definition.Contact
import love.forte.simbot.definition.Guild

/**
 * 一个 `Bot`。
 *
 * @author ForteScarlet
 */
public interface Bot : IDContainer, LifecycleAware, CompletionAware, CoroutineScope, BotRelation {
    /**
     * 当前bot的标识。
     *
     * 此标识可能是 bot 在系统中的 id （例如某种用户ID），
     * 也可能只是注册此 bot 时使用一种标识
     * （比如向平台申请并下发的某种 `bot_id` 或者 `token` ）。
     *
     * 通常情况下，它会是注册bot时候使用的某种唯一标识。
     *
     */
    public override val id: ID

    /**
     * 当前 [Bot] 作为用户的名称。
     *
     * 通常 [name] 只有在当前 [Bot] 启动后才可能被获取，或者需要通过其他 API 主动获取。
     * 这类主动获取信息的API由实现者自由定义。
     *
     * [name] 作为一种属性（非挂起）无法保证实时性，
     * 除非存在可以通知 [Bot] 名称变更的事件以供刷新内部缓存。
     *
     * @throws IllegalStateException 无法获取时，例如 [Bot] 尚未启动或没有任何可用的内部缓存。
     */
    public val name: String

    /**
     * 此 bot 所属 [Component]。
     */
    public val component: Component

    /**
     * 当可能有多个 id 用来与当前 [Bot] 进行对应时
     * （例如 [Bot] 作为用户时的 id 以及注册 bot 时使用的 token）
     * 通过 [isMe] 来判断指定 id 是否可以用来表示当前 [Bot]。
     *
     */
    public infix fun isMe(id: ID): Boolean

    /**
     * 是否支持 [GuildRelation] 中提供的任意 API 能力。
     */
    public val isAnyGuildRelationSupported: Boolean

    /**
     * 是否支持 [GuildRelation] 中提供的所有 API 能力。
     */
    public val isAllGuildRelationSupported: Boolean

    /**
     * 是否支持 [GroupRelation] 中提供的任意 API 能力。
     */
    public val isAnyGroupRelationSupported: Boolean

    /**
     * 是否支持 [GroupRelation] 中提供的所有 API 能力。
     */
    public val isAllGroupRelationSupported: Boolean

    /**
     * 是否支持 [ContactRelation] 中提供的任意 API 能力。
     */
    public val isAnyContactRelationSupported: Boolean

    /**
     * 是否支持 [ContactRelation] 中提供的所有 API 能力。
     */
    public val isAllContactRelationSupported: Boolean

    /**
     * 启动当前 [Bot]。
     *
     * 如果 [Bot] 已经启动，则重复调用 [start] 可能会无效果，
     * 也可能会使 [Bot] **重新启动**。这取决于 [Bot] 的实现。
     * 更建议以后者为标准实现。
     *
     */
    @JST
    public suspend fun start()

    /**
     * 是否 **启动过**。
     *
     * 当调用过至少一次 [start] 且未产生异常后 [isStarted] 将会得到 `true`。
     * [isStarted] 与当前 [Bot] 是否被关闭无关，也不会被其影响。
     */
    public val isStarted: Boolean

    // join & cancel

    /**
     * 挂起 [Bot] 直到它完成其生命周期。
     */
    @JST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()

    /**
     * 关闭当前 [Bot]。
     *
     * 当 [Bot] 被完全关闭时，[join] 会结束挂起。
     * 效果与 [CoroutineScope.cancel] 类似。
     */
    public fun cancel(reason: Throwable?)

    /**
     * 关闭当前 [Bot]。
     *
     * 当 [Bot] 被完全关闭时，[join] 会结束挂起。
     * 效果与 [CoroutineScope.cancel] 类似。
     */
    public fun cancel() {
        cancel(null)
    }

}

/**
 * [Bot] 与部分行为对象的关系接口，继承 [GuildRelation]、[GroupRelation]、[ContactRelation] 并由 [Bot] 实现。
 *
 * [BotRelation] 不提供与 [Channel] 直接相关的 API 或接口，对其的操作可以通过 [Guild] 间接完成，
 * 或在实现者支持直接操作 [Channel] 时提供额外的 API。
 *
 * @see Bot
 * @see GuildRelation
 * @see GroupRelation
 * @see ContactRelation
 */
public interface BotRelation : GuildRelation, GroupRelation, ContactRelation


/**
 * 与频道服务器的关系。可用于寻找指定的频道服务器或查询频道服务器的集合。
 *
 */
public interface GuildRelation {
    /**
     * 根据ID寻找指定的频道服务器。如果找不到则得到 `null`。
     *
     * 如果实现者不支持也可能始终得到 `null`。
     */
    @JST(blockingBaseName = "getGuild", blockingSuffix = "", asyncBaseName = "getGuild")
    public suspend fun guild(id: ID): Guild?

    /**
     * 获取所有可知的，或者说 [Bot] 当前所加入/所在的所有频道服务器集合。
     *
     * 如果实现者不支持也可能始终得到空集合。
     */
    public val guilds: Collectable<Guild>

    /**
     * 获取当前频道服务器的总数量，不为负时有效。
     * 当不支持此 API 的时候返回小于0的值。
     *
     * 没有 **直接** 支持的 API 时，
     * 可能会通过 [guilds] 或者API查询所有数据的方式来获取总数。
     */
    @JSTP
    public suspend fun guildCount(): Int
}

/**
 * 与聊天群的关系。可用于寻找指定的聊天群或查询聊天群的集合。
 *
 */
public interface GroupRelation {
    /**
     * 根据ID寻找指定的聊天群。如果找不到则得到 `null`。
     *
     * 如果实现者不支持也可能始终得到 `null`。
     */
    @JST(blockingBaseName = "getGroup", blockingSuffix = "", asyncBaseName = "getGroup")
    public suspend fun group(id: ID): ChatGroup?

    /**
     * 获取所有可知的，或者说 [Bot] 当前所加入/所在的所有聊天群集合。
     *
     * 如果实现者不支持也可能始终得到空集合。
     */
    public val groups: Collectable<ChatGroup>

    /**
     * 获取当前频道服务器的总数量，不为负时有效。
     * 当不支持此 API 的时候返回小于0的值。
     *
     * 没有 **直接** 支持的 API 时，
     * 可能会通过 [groups] 或者API查询所有数据的方式来获取总数。
     */
    @JSTP
    public suspend fun groupCount(): Int
}

/**
 * 与联系人的关系。可用于寻找指定的联系人或查询联系人/会话的集合。
 */
public interface ContactRelation {
    /**
     * 寻找一个指定ID的联系人，如果找不到则得到 `null`。
     *
     * 如果联系人是一种会话，则 [contact] 可能会尝试“创建”会话。在这种情况下，
     * [contact] 不会得到 `null`。
     *
     * 如果实现者不支持也可能始终得到 `null`。
     */
    @JST(blockingBaseName = "getContact", blockingSuffix = "", asyncBaseName = "getContact")
    public suspend fun contact(id: ID): Contact?

    /**
     * 获取所有可知的联系人或联系人会话。
     *
     * 对于一个普通的 bot 来讲，此处的联系人可能是某种持有的**会话**，而并非普通用户那种“好友关系”。
     * 因此并非所有能够通过 [contact] 获取（或“创建”）的联系人/联系人会话都能在 [contacts] 中出现。
     *
     * 如果实现者不支持也可能始终得到空集合。
     */
    public val contacts: Collectable<Contact>

    /**
     * 获取当前联系人或联系人会话的总数量，不为负时有效。
     * 当不支持此 API 的时候返回小于0的值。
     *
     * 没有 **直接** 支持的 API 时，
     * 可能会通过 [contacts] 或者API查询所有数据的方式来获取总数。
     */
    @JSTP
    public suspend fun contactCount(): Int
}

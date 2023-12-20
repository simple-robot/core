package love.forte.simbot.quantcat.annotations

import love.forte.simbot.common.PriorityConstant
import love.forte.simbot.quantcat.MatchType
import love.forte.simbot.quantcat.annotations.Filter.Targets

/**
 * 与 [@Listener][Listener] 配合使用，标记为对当前事件处理器的基础属性过滤器。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class Filter(
    /**
     * 基于一定规则，对 **消息事件** 的 [**纯文本内容**][love.forte.simbot.message.MessageContent.plainText] 进行逻辑匹配。
     *
     * ## 参数提取
     *
     * 当 [matchType] 为正则相关的匹配时（例如 [MatchType.REGEX_MATCHES]、[MatchType.REGEX_CONTAINS] 等），
     * 可以通过占位符 `{{name[,regex]}}` （例如 `age:{{age,\\d+}}`、`name:{{name}}`）
     * 或正则的 name group (参考:
     * [regular-expressions: Named Capturing Groups and Backreferences](https://www.regular-expressions.info/named.html)、
     * [Java Pattern: named-capturing group](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) 或其他相关网站)
     * `(?<name>group)` （例如 `age:(?<age>\\d+)`、`name:(?<name>.+)`）
     * 来提取某个匹配到的变量（通过注解 [FilterValue] 标记参数获取）。
     * 其中，占位符的形式 `{{...}}` 会在解析时转化为正则 name group 的形式，其二者的最终原理是相同的，最终基于 [Regex] 实现。
     */
    val value: String = "",
    /**
     * [Filter] 所产生的“过滤器”的实现模式。默认为注入到事件处理器逻辑之前。
     */
    val mode: FilterMode = FilterMode.IN_LISTENER,
    /**
     * 当 [mode] 为 [FilterMode.INTERCEPTOR] 时可用，代表其作为拦截器注册时的优先级。
     */
    val priority: Int = PriorityConstant.NORMAL,
    /**
     * 针对部分特定目标的过滤匹配。
     *
     * 建议只提供 0-1 个 [Targets] 。
     * 当提供多个 [Targets] 时，最终的匹配内容为它们的合并结果。
     * 但是多个 [Targets] 并不美观，也会使整体代码显得冗长。
     */
    val targets: Array<Targets> = [],
    /**
     * 当 [value] 对消息事件进行匹配时，
     * 如果消息的 [纯文本内容][love.forte.simbot.message.MessageContent.plainText] 为 `null`，
     * 是否直接放行。如果为 `true`, 则纯文本内容为 `null` 的时候视为匹配通过，反之则为匹配失败。默认为 `false`。
     * 此参数只有当 `value` 不为空的时候有效。
     *
     */
    val ifNullPass: Boolean = false,
    // 这并不仅仅局限于消息事件, 而生效与所有的事件类型。而对于那些本身即不是消息事件的事件来说, textContent 默认为null。 ?

    /**
     * 针对匹配目标所使用的匹配规则。
     * 默认情况下使用 [正则完全匹配][MatchType.REGEX_MATCHES].
     */
    val matchType: MatchType = MatchType.REGEX_MATCHES

    // TODO by?

) {

    /**
     * 针对部分特定目标的过滤匹配。
     */
    @Retention(AnnotationRetention.SOURCE)
    @Target(allowedTargets = [])
    public annotation class Targets(
        /**
         * 对 [Component][love.forte.simbot.component.Component] 进行匹配。
         * 如果事件为 [BotEvent][love.forte.simbot.event.BotEvent],
         * 则只有 [Bot.component.id][love.forte.simbot.component.Component.id] 在此列表中时才会放行。
         *
         */
        val components: Array<String> = [],
        /**
         * 对 [Bot][love.forte.simbot.bot.Bot] 进行匹配。
         * 如果事件为 [BotEvent][love.forte.simbot.event.BotEvent],
         * 则只有 [Bot.id][love.forte.simbot.bot.Bot.id] 在此列表中时才会放行。
         *
         * ```kotlin
         * event.bot.id in bots
         * ```
         *
         * @see checkBotsIdByIsMe
         */
        val bots: Array<String> = [],

        /**
         * 是否使用 [Bot.isMe][love.forte.simbot.bot.Bot.isMe] 作为 [bots]
         * 的匹配方式。
         *
         * ```kotlin
         * bots.any { botId -> event.bot.isMe(botId) }
         * ```
         *
         * 默认为 `false`。
         */
        val checkBotsIdByIsMe: Boolean = false,

        /**
         * 对 [Actor][love.forte.simbot.definition.Actor] 进行匹配。
         * 如果事件为 [ActorEvent][love.forte.simbot.event.ActorEvent],
         * 则只有 [Actor.id][love.forte.simbot.definition.Actor.id] 在此列表中时才会放行。
         *
         * ```kotlin
         * event.content().id in actors
         * ```
         *
         */
        val actors: Array<String> = [],

        /**
         * 对消息发送者进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.authorId][love.forte.simbot.event.MessageEvent.authorId]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.authorId in authors
         * ```
         */
        val authors: Array<String> = [],

        /**
         * 对事件的 [ChatRoom][love.forte.simbot.definition.ChatRoom] 进行匹配。
         * 如果事件为 [ChatRoomEvent][love.forte.simbot.event.ChatRoomEvent]，
         * 则只有 [ChatRoomEvent.content.id][love.forte.simbot.definition.ChatRoom.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in chatRooms
         * ```
         */
        val chatRooms: Array<String> = [],

        /**
         * 对事件的 [Organization][love.forte.simbot.definition.Organization] 进行匹配。
         * 如果事件为 [OrganizationEvent][love.forte.simbot.event.OrganizationEvent]，
         * 则只有 [OrganizationEvent.content.id][love.forte.simbot.definition.Organization.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in organizations
         * ```
         */
        val organizations: Array<String> = [],

        /**
         * 对事件的 [ChatGroup][love.forte.simbot.definition.ChatGroup] 进行匹配。
         * 如果事件为 [ChatGroupEvent][love.forte.simbot.event.ChatGroupEvent]，
         * 则只有 [ChatGroupEvent.content.id][love.forte.simbot.definition.ChatGroup.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in groups
         * ```
         */
        val groups: Array<String> = [],

        /**
         * 对事件的 [Guild][love.forte.simbot.definition.Guild] 进行匹配。
         * 如果事件为 [GuildEvent][love.forte.simbot.event.GuildEvent]，
         * 则只有 [GuildEvent.content.id][love.forte.simbot.definition.Guild.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in guilds
         * ```
         */
        val guilds: Array<String> = [],

        /**
         * 对事件的 [Contact][love.forte.simbot.definition.Contact] 进行匹配。
         * 如果事件为 [ContactEvent][love.forte.simbot.event.ContactEvent]，
         * 则只有 [ContactEvent.content.id][love.forte.simbot.definition.Contact.id]
         * 在此列表中才会放行。
         *
         * ```kotlin
         * event.content.id in contacts
         * ```
         */
        val contacts: Array<String> = [],

        // messages

        /**
         * 对消息事件中 [At][love.forte.simbot.message.At] 进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.messageContent.messages][love.forte.simbot.message.MessageContent.messages]
         * 中存在 [At][love.forte.simbot.message.At] 消息且包含下述 **任意** at 目标时才会放行。
         *
         * ```kotlin
         * event.messageContent.messages.any { m -> m is At && m.id in atAny }
         * ```
         */
        val ats: Array<String> = [],

        /**
         * 对消息事件中 [At][love.forte.simbot.message.At] 进行匹配。
         * 如果事件为 [MessageEvent][love.forte.simbot.event.MessageEvent]，
         * 则只有 [MessageEvent.messageContent.messages][love.forte.simbot.message.MessageContent.messages]
         * 中存在 [At][love.forte.simbot.message.At] 消息且 id 属于事件 `bot` 时才会放行。
         *
         * ```kotlin
         * event.messageContent.messages.any { m -> m is At && event.bot.isMe(m.id) }
         * ```
         */
        val atBot: Boolean = false,

        // val explicitTypeCheck: Boolean = false,
    ) {
        public companion object {
            /**
             * [Filter.Targets] 中的“非”前缀。
             *
             */
            public const val NON_PREFIX: String = "!"
        }
    }


}


/**
 * [Filter] 标记的结果最终产生的“过滤器”的模式。
 *
 */
public enum class FilterMode {
    /**
     * 将 [Filter] 中的逻辑作为 [EventInterceptor][love.forte.simbot.event.EventInterceptor] 注册。
     * 可以通过优先级的控制来使其与其他全局拦截器之间的关系。
     *
     */
    INTERCEPTOR,

    /**
     * 作为一段逻辑注入到事件处理器的前置中。
     * 由于最终的执行逻辑是与事件处理器的逻辑融为一体的，
     * 所以使用此模式时，[Filter] 所产生的逻辑始终会在所有拦截器之后执行。
     */
    IN_LISTENER


}


internal val EmptyTargets: Targets = Targets()

/**
 * Combines two instances of the [Targets] class together.
 *
 * @param other The [Targets] instance to be combined with.
 * @return The resulting [Targets] instance after the combination.
 */
public operator fun Targets.plus(other: Targets): Targets {
    if (this == EmptyTargets) {
        if (other == EmptyTargets) {
            return EmptyTargets
        }
        return other
    }

    if (other == EmptyTargets) {
        if (this == EmptyTargets) {
            return EmptyTargets
        }
        return this
    }

    return mergeTargets(listOf(this, other))
}

/**
 * Merges the given list of [Targets] into a single [Targets] object.
 *
 * @param targets The list of [Targets] to merge.
 * @return The merged [Targets] object.
 */
public fun mergeTargets(targets: Iterable<Targets>): Targets {
    val iterator = targets.iterator()
    if (!iterator.hasNext()) {
        return EmptyTargets
    }

    val first = iterator.next()
    if (!iterator.hasNext()) {
        return first
    }

    var checkBotsIdByIsMe = first.checkBotsIdByIsMe
    var atBot = first.atBot

    val components = first.components.toMutableSet()
    val bots = first.bots.toMutableSet()
    val actors = first.actors.toMutableSet()
    val authors = first.authors.toMutableSet()
    val chatRooms = first.chatRooms.toMutableSet()
    val organizations = first.organizations.toMutableSet()
    val groups = first.groups.toMutableSet()
    val guilds = first.guilds.toMutableSet()
    val contacts = first.contacts.toMutableSet()
    val ats = first.ats.toMutableSet()

    for (value in iterator) {
        checkBotsIdByIsMe = value.checkBotsIdByIsMe || checkBotsIdByIsMe
        atBot = value.atBot || atBot

        components.addAll(value.components)
        bots.addAll(value.bots)
        actors.addAll(value.actors)
        authors.addAll(value.authors)
        chatRooms.addAll(value.chatRooms)
        organizations.addAll(value.organizations)
        groups.addAll(value.groups)
        guilds.addAll(value.guilds)
        contacts.addAll(value.contacts)
        ats.addAll(value.ats)
    }

    val allEmpty = components.isEmpty() &&
            bots.isEmpty() &&
            actors.isEmpty() &&
            authors.isEmpty() &&
            chatRooms.isEmpty() &&
            organizations.isEmpty() &&
            groups.isEmpty() &&
            guilds.isEmpty() &&
            contacts.isEmpty() &&
            ats.isEmpty()

    if (allEmpty && EmptyTargets.atBot == atBot && EmptyTargets.checkBotsIdByIsMe == checkBotsIdByIsMe) {
        return EmptyTargets
    }

    return Targets(
        atBot = atBot,
        checkBotsIdByIsMe = checkBotsIdByIsMe,
        components = components.toTypedArray(),
        bots = bots.toTypedArray(),
        actors = actors.toTypedArray(),
        authors = authors.toTypedArray(),
        chatRooms = chatRooms.toTypedArray(),
        organizations = organizations.toTypedArray(),
        groups = groups.toTypedArray(),
        guilds = guilds.toTypedArray(),
        contacts = contacts.toTypedArray(),
        ats = ats.toTypedArray(),
    )
}

/**
 * 配合 [Filter] 使用。针对多个 [Filter] 之间的协同配置。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
public annotation class MultiFilter(
    /**
     * 多个过滤器之间的匹配策略。
     */
    val matchType: MultiFilterMatchType
)

/**
 * 多值匹配，当可能存在多轮匹配时进行的取值策略。
 *
 * @see MultiFilter
 * @author ForteScarlet
 *
 */
public enum class MultiFilterMatchType {
    /**
     * 任意匹配成功即可
     */
    ANY,

    /**
     * 需要全部匹配成功
     */
    ALL,

    /**
     * 需要无匹配内容
     */
    NONE;
}

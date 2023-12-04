package love.forte.simbot.bot

import love.forte.simbot.plugin.Plugin
import love.forte.simbot.resource.StringResource

/**
 * [Bot] 的工厂。
 *
 * [BotFactory] 的实现者通常会提供额外的注册API来支持更多样便捷的个性化注册。
 *
 * @author ForteScarlet
 * @see BotManager
 *
 */
public interface BotFactory : Plugin {
    /**
     * 通用的、基于配置文件资源的 bot 注册API。
     *
     * 提供一个此 [BotFactory] 支持的格式的配置文件资源 [resource] 和此文件的格式 [format] ，
     * [BotFactory] 尝试解析并注册为一个 [Bot]。
     *
     * 通常 [BotFactory] 都应提供更多友好的注册API。
     * 在知道 [BotFactory] 具体类型的情况下可选择更具体的注册API。
     *
     * @param resource 配置文件资源
     * @param format 配置文件内容的格式。参考 [StringFormatTarget] 的文档说明
     *
     * @throws ConflictBotException 如果 [BotFactory] 不支持重复的 `bot` 注册，且注册了重复的 `bot`
     * @throws BotRegisterFailureException bot 注册失败异常
     * @throws UnsupportedStringFormatTargetException 当提供了不支持的 [format] 时
     */
    public fun register(resource: StringResource, format: StringFormatTarget)
}

/**
 * 使用在 [BotFactory.register] 中的格式化目标类型。
 *
 * 此接口用于提供 [BotFactory] 的实现者支持的配置文件格式。
 *
 * [StringFormatTarget] 不提供任何约束，由实现者自由扩展。
 * 但是无论 [BotFactory] 是否扩展 [StringFormatTarget]，
 * 都应当至少支持 [StandardStringFormatTarget] 中提供的格式类型。
 *
 * @see StandardStringFormatTarget
 */
public interface StringFormatTarget

/**
 * 标准的 [StringFormatTarget]。任何 [BotFactory] 的实现都应当至少支持的目标类型。
 *
 */
public enum class StandardStringFormatTarget : StringFormatTarget {
    /**
     * `JSON` 格式。
     */
    JSON
}

/**
 * @see BotFactory.register
 */
public open class UnsupportedStringFormatTargetException : IllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * @see BotFactory.register
 */
public open class BotRegisterFailureException : BotException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

package love.forte.simbot.spring

import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.SerializableBotConfiguration

/**
 * 当解析函数为事件处理器时，参数中出现了多个不兼容的事件类型时的异常。
 */
public open class MultipleIncompatibleTypesEventException : IllegalArgumentException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 当自动扫描的bot的配置文件加载失败时（找不到文件、无法读取、无法解析为 [SerializableBotConfiguration] 等）。
 *
 */
public open class BotConfigResourceLoadOnFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 如果没有匹配的 [BotManager] 可供注册。
 */
public open class MismatchConfigurableBotManagerException(s: String?) : NoSuchElementException(s)

/**
 * 当 Bot 自动启动时出现了错误并失败了
 *
 */
public open class BotAutoStartOnFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

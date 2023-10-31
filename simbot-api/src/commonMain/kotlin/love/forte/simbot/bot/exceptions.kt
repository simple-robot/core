package love.forte.simbot.bot

/**
 * 与 [Bot] 或 [BotManager] 等相关的异常类型。
 */
public open class BotException : RuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 当没有找到指定 [Bot] 时。
 * @see BotManager.get
 */
public open class NoSuchBotException : BotException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

// 冲突的ID

/**
 * 当 [Bot] 在注册、获取等情况下发生冲突时。通常体现为 [Bot.id] 的重复。
 */
public open class ConflictBotException : BotException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

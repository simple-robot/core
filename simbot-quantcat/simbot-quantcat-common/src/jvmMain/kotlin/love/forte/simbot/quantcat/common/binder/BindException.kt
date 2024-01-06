package love.forte.simbot.quantcat.common.binder


/**
 * 当 [ParameterBinder.arg] 中出现了异常。
 */
public open class BindException : IllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

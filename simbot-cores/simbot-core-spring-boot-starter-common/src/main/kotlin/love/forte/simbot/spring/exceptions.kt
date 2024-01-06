package love.forte.simbot.spring

public class Du

/**
 * 当解析函数为事件处理器时，参数中出现了多个不兼容的事件类型时的异常。
 */
public open class MultipleIncompatibleTypesEventException : IllegalArgumentException {
    public constructor() : super()
    public constructor(s: String?) : super(s)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

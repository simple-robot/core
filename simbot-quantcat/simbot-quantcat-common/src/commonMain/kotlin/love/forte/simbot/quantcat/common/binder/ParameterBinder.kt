package love.forte.simbot.quantcat.common.binder

import love.forte.simbot.event.EventListenerContext

/**
 * 监听函数动态参数的绑定器。通过所需的执行参数而得到的参数绑定器。
 *
 * 对于一个可执行函数的参数 `KParameter` 所需的结果获取器。
 *
 */
public interface ParameterBinder {
    /**
     * 根据当前事件处理上下文得到参数值。
     *
     * 如果出现无法为当前参数提供注入的情况，通过返回 [Result.Failure] 或抛出异常来提示处理器使用下一个顺序的处理器。
     *
     * 如果参数为可选的，可以返回标记类型 [Ignore] 来代表本次忽略参数值。
     *
     * @throws Throwable 其他预期外的异常。
     */
    public fun arg(context: EventListenerContext): Result<Any?>

    /**
     * 在 [arg] 的返回值中所使用的标记类型，当 [arg] 的返回值为 [Ignore] 的时候，
     * 则代表忽略此参数的使用。通常用在可选参数中。
     */
    public object Ignore
}


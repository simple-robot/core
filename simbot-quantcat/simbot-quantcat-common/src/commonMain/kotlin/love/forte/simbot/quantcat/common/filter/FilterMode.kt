package love.forte.simbot.quantcat.common.filter
/**
 * [Filter] 标记的结果最终产生的“过滤器”的模式。
 *
 */
public enum class FilterMode {
    /**
     * 将 `Filter` 中的逻辑作为 [EventInterceptor][love.forte.simbot.event.EventInterceptor] 注册。
     * 可以通过优先级的控制来使其与其他全局拦截器之间的关系。
     *
     */
    INTERCEPTOR,

    /**
     * 作为一段逻辑注入到事件处理器的前置中。
     * 由于最终执行逻辑是与事件处理器的逻辑融为一体的，
     * 所以使用此模式时，`Filter` 所产生的逻辑始终会在所有拦截器之后执行。
     */
    IN_LISTENER
}

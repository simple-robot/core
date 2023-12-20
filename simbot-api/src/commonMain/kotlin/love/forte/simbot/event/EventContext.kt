package love.forte.simbot.event

import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.message.MessageContent

/**
 * 一个在事件处理流程中流转的上下文。
 * 用于承载本次事件处理前后的诸项信息。
 *
 * @author ForteScarlet
 */
public interface EventContext {
    /**
     * 本次事件处理流程中被处理的事件。
     */
    public val event: Event

    /**
     * 可以通过 [EventContext] 相互传递的 [MutableAttributeMap]。
     */
    public val attributes: MutableAttributeMap
}

/**
 * 一个在每一个事件处理器的独立处理流程中流转的上下文。
 * 用于承载本次事件处理前后的诸项信息。
 *
 * @author ForteScarlet
 */
public interface EventListenerContext {
    /**
     * 整个事件调度流程中的 [EventContext]。
     */
    public val context: EventContext

    /**
     * 获取 [context] 中的 [event][EventContext.event]
     *
     * @see EventContext.event
     */
    public val event: Event get() = context.event

    /**
     * 本次事件处理器进行处理时，用于**匹配**的事件中消息文本内容。
     * 如果为 `null` 则可能说明事件类型不是 [MessageEvent] 或 [MessageContent.plainText] 本身为 `null`。
     * 建议在使用 [EventInterceptor] 或逻辑内有效性匹配时，如果需要对事件的文本内容进行处理、匹配，使用此处的 [plainText]
     * 而不是 [MessageContent.plainText]。
     * 一些自动生成、处理或流程化的处理逻辑（例如 `quantcat` 相关模块中的注解形式处理器）也会使用此处的 [plainText] 并可能对其值造成影响。
     *
     * [plainText] 是 **可修改** 的，其值的修改**不会**影响到事件原本的值。
     * 此值服务于流程化的拦截器以及匹配逻辑。
     */
    public var plainText: String?
}



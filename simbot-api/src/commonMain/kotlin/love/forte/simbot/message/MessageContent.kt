package love.forte.simbot.message

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.id.ID


/**
 * 一个消息本体内容。通常是通过一个事件接收到的消息本体。
 *
 * ### [DeleteSupport]
 *
 * [MessageContent] 实现 [DeleteSupport]，提供可能支持的删除行为。更多说明参考 [MessageContent.delete]
 *
 * @author ForteScarlet
 */
public interface MessageContent : DeleteSupport {
    /**
     * 消息的ID。
     *
     * 如果消息来源（例如推送的事件）支持消息ID，
     * 则此处即为原始的消息ID；如果来源不支持或不存在ID，则此处可能为一个随机ID。
     */
    public val id: ID

    /**
     * 消息本体中完整消息的消息链。
     *
     */
    public val messages: Messages

    /**
     * 消息本体中提取出的所有普通文本消息的合并结果。
     *
     * 类似于:
     * ```kotlin
     * messages.filterIsInstance<PlainText>().joinToString { it.text }
     * ```
     *
     * @see messages
     *
     */
    public val plainText: String

    /**
     * 删除、撤回、撤销此消息。
     *
     * 如果实现者不支持这类API，则会抛出 [UnsupportedOperationException]。
     * 如果删除过程中由于权限、不存在等各种业务原因导致的删除失败，则应抛出异常，
     * 并建议使用或扩展 [DeleteFailureException] 异常类型。
     *
     * @param options 删除时的可选选项。不支持的选项将会被忽略。更多说明参考 [DeleteOption]。
     *
     * @throws UnsupportedOperationException API本身不被实现者支持
     * @throws DeleteFailureException 删除行为失败
     * @throws NoSuchElementException 没有可删除目标
     */
    override suspend fun delete(vararg options: DeleteOption)
}

package love.forte.simbot.definition

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import love.forte.simbot.bot.Bot
import love.forte.simbot.id.ID

/**
 *
 * 一个行为主体。
 *
 * [Actor] 是可能具有行为的目标的父类型，
 * 例如一个聊天室（群聊、文字频道等）或一个联系人（`Contact`）或组织成员（`Member`）。
 *
 * ### [CoroutineScope]
 *
 * [Actor] 继承 [CoroutineScope]，提供一个与所属 [Bot] 相关的作用域。
 * [Actor] 所描述的协程作用域可能与 [Bot] 相同、可能属于 [Bot] 的子作用域，也可能不存在 [Job]。
 * 如果 `cancel` 一个 [Actor] 也可能会导致与之关联的 [Bot] 被关闭，也可能无法关闭。
 * 这一切取决于 [Actor] 的具体实现。
 *
 * @author ForteScarlet
 */
public interface Actor : CoroutineScope {
    /**
     * 行为主体的唯一标识。
     */
    public val id: ID
}



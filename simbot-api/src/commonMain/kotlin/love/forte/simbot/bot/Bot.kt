package love.forte.simbot.bot

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.id.ID

/**
 * 一个 `Bot`。
 *
 * @author ForteScarlet
 */
public interface Bot : LifecycleAware, CompletionAware, CoroutineScope {
    /**
     * 当前bot的标识。
     *
     * 此标识可能是 bot 在系统中的 id （例如某种用户ID），
     * 也可能只是注册此 bot 时使用一种标识
     * （比如向平台申请并下发的某种 `bot_id` 或者 `token` ）。
     *
     * 通常情况下，它会是注册bot时候使用的某种唯一标识。
     *
     */
    public val id: ID

}

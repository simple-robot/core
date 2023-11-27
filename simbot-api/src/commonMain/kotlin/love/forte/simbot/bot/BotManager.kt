package love.forte.simbot.bot

import love.forte.simbot.ability.LifecycleAware
import love.forte.simbot.id.ID

/**
 *
 * [Bot] 的管理器。
 *
 * ### 生命周期
 *
 * [BotManager] 持有一个会影响诞生自它的所有 [Bot] 的生命周期。
 * 当 [BotManager] 被执行了 [BotManager.cancel]，除了影响 [BotManager] 自身的生命周期以外，
 * 也会同样影响到所有由它产生的 [Bot]。
 *
 *
 * @author ForteScarlet
 * @see BotFactory
 */
public interface BotManager : BotFactory, LifecycleAware {

    /**
     * 得到所有的 [Bot]，以序列的形式。
     */
    public fun all(): Sequence<Bot>

    /**
     * 得到所有 `id` 符合条件的 [Bot]，以序列的形式。
     */
    public fun all(id: ID): Sequence<Bot> = all().filter { bot -> bot.id == id }

    /**
     * 根据一个指定的 [id] 获取匹配的bot。
     *
     * 如果当前管理的 [Bot] 中没有匹配的结果则会抛出 [NoSuchBotException]。
     *
     * [BotManager] 不保证所有的 [Bot] 的 id 是唯一的，如果当前
     * [BotManager] 允许存在多个 `id` 相同的 [Bot]，那么当获取的 [id]
     * 出现冲突时（例如存在两个或以上的 [Bot]）则会抛出 [ConflictBotException]。
     * 如果希望避免此问题，可考虑使用 [all] 自行筛选。
     *
     * @throws ConflictBotException 如果存在重复 id 的 [Bot]
     * @throws NoSuchBotException 如果不存在
     */
    public operator fun get(id: ID): Bot

    /**
     * 根据一个指定的 [id] 寻找匹配的bot。
     *
     * [BotManager] 不保证所有的 [Bot] 的 id 是唯一的，如果当前
     * [BotManager] 允许存在多个 `id` 相同的 [Bot]，那么当获取的 [id]
     * 出现冲突时（例如存在两个或以上的 [Bot]）则会抛出 [ConflictBotException]。
     *
     * @throws ConflictBotException 如果存在重复 id 的 [Bot]
     */
    public fun find(id: ID): Bot? = try {
        get(id)
    } catch (nb: NoSuchBotException) {
        null
    }

    /**
     * 挂起直到被 [cancel]。
     *
     * 即使一个 [BotManager] 没有管理任何 [Bot]，
     * 在 [cancel] 之前也会保持挂起状态。
     */
    public suspend fun join()

    /**
     * 关闭当前 [BotManager]. 会同时关闭由其管理的所有 [Bot]。
     */
    public fun cancel(cause: Throwable? = null)
}

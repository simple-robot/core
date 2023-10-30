package love.forte.simbot.bot

/**
 *
 * @author ForteScarlet
 */
public interface BotManager : BotFactory {




    /**
     * 关闭当前 [BotManager]. 会同时关闭由其管理的所有 [Bot]。
     */
    public fun cancel(cause: Throwable? = null)
}

package love.forte.simbot.ability

/**
 * 提供一些可以获取到当前状态的感知类型。
 *
 * 通常情况下会配合 [kotlinx.coroutines.Job] 实现。
 *
 * @author ForteScarlet
 */
public interface LifecycleAware {
    /**
     * 当前是否处于活跃、运行或尚未结束的状态。
     */
    public val isActive: Boolean

    /**
     * 当前是否已经完成、已经结束。
     */
    public val isCompleted: Boolean
}

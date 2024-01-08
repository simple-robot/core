package love.forte.simbot.bot

import kotlinx.coroutines.Job
import love.forte.simbot.ability.OnCompletion
import kotlin.jvm.JvmSynthetic
import kotlinx.coroutines.CancellationException as CreateCancellationException


/**
 * 一个基于 [Job] 并提供部分基础能力实现的 [BotManager] 抽象。
 *
 * @author ForteScarlet
 */
public abstract class JobBasedBotManager : BotManager {
    protected abstract val job: Job

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted


    @JvmSynthetic
    override suspend fun join() {
        job.join()
    }

    override fun cancel(cause: Throwable?) {
        job.cancel(cause?.let { CreateCancellationException(it.message, it) })
    }

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { cause -> handle.invoke(cause) }
    }


}

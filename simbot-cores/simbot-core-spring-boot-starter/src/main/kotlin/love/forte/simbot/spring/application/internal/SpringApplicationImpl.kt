package love.forte.simbot.spring.application.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.application.*
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.spring.application.SpringApplication
import kotlin.coroutines.CoroutineContext


/**
 * @author ForteScarlet
 */
internal class SpringApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val eventDispatcher: EventDispatcher,
    override val components: Components,
    override val plugins: Plugins,
    override val botManagers: BotManagers,
    val events: ApplicationLaunchStages
) : SpringApplication {
    private val job: Job
    override val coroutineContext: CoroutineContext

    init {
        val newJob = SupervisorJob(configuration.coroutineContext[Job])
        val newCoroutineContext = configuration.coroutineContext.minusKey(Job) + newJob

        this.job = newJob
        this.coroutineContext = newCoroutineContext
    }

    private inline fun <C : Any, reified H : NormalApplicationEventHandler<C>> invokeNormalHandler(
        stage: ApplicationLaunchStage<H>, block: H.() -> Unit
    ) {
        events[stage]?.forEach { handler ->
            (handler as? H)?.also { handler0 ->
                block(handler0)
            }
        }
    }

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { handle.invoke(it) }
    }

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted

    override fun cancel(reason: Throwable?) {
        invokeNormalHandler(ApplicationLaunchStage.RequestCancel) {
            invoke(this@SpringApplicationImpl)
        }

        job.cancel(reason?.let { CancellationException(reason.message, it) })

        invokeNormalHandler(ApplicationLaunchStage.Cancelled) {
            invoke(this@SpringApplicationImpl)
        }
    }

    override suspend fun join() {
        job.join()
    }

    override fun toString(): String {
        return "SpringApplication(isActive=$isActive, isCompleted=$isCompleted, eventDispatcher=$eventDispatcher, components=$components, plugins=$plugins)"
    }


}

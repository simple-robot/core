@file:JvmName("Applications")
@file:JvmMultifileClass

package love.forte.simbot.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.function.toConfigurerFunction
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

/**
 * 将 [Application] 转作 [CompletableFuture]。
 *
 * @see Job.asCompletableFuture
 */
public fun Application.asCompletableFuture(): CompletableFuture<Unit> =
    coroutineContext[Job]?.asCompletableFuture() ?: CompletableFuture.completedFuture(Unit)


/**
 * 构建一个 [Application] 并阻塞地启动它。
 *
 * 启动过程中产生了任何异常，都会被包装在 [ApplicationLaunchBlockingFailureException] 中
 *
 * @throws ApplicationLaunchBlockingFailureException 启动过程中产生了任何异常的包装
 */
@JvmOverloads
public fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplicationBlocking(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): A {
    runCatching {
        val launcher = factory.create(configurer?.let { c ->
            toConfigurerFunction {
                c.invokeWith(this)
            }
        })

        return runInNoScopeBlocking { launcher.launch() }
    }.getOrElse { e ->
        throw ApplicationLaunchBlockingFailureException(e)
    }
}

/**
 * @see launchApplicationBlocking
 */
public class ApplicationLaunchBlockingFailureException internal constructor(cause: Throwable?) : RuntimeException(cause)


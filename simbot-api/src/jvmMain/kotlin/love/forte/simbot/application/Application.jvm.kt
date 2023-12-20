@file:JvmName("Applications")
@file:JvmMultifileClass

package love.forte.simbot.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.annotations.InternalAPI
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

// TODO

/**
 * 提供给 [Components]、[Plugins]、[BotManagers] 实现的平台额外能力的接口。
 *
 * [PlatformCollection] 在 JVM 平台提供更多扩展默认方法，例如使用 `Class` 过滤或寻找目标结果的方法。
 *
 */
@InternalAPI
public actual interface PlatformCollection<out T> : Collection<T> {
    /**
     * 寻找第一个类型为 [type] 的目标。
     * 如果没有则得到 `null`。
     */
    public fun <R : @UnsafeVariance T> find(type: Class<R>): R? =
        this.find { type.isInstance(it) }?.let { type.cast(it) }


    /**
     * 寻找第一个类型为 [type] 的目标。
     * 如果没有则抛出 [NoSuchElementException]。
     *
     * @throws NoSuchElementException 如果没找到目标结果
     */
    public fun <R : @UnsafeVariance T> get(type: Class<R>): R =
        this.find { type.isInstance(it) }?.let { type.cast(it) } ?: throw NoSuchElementException(type.toString())
}

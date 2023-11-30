@file:JvmMultifileClass
@file:JvmName("Applications")

package love.forte.simbot.application

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import love.forte.simbot.async.Async
import love.forte.simbot.async.toAsync
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.function.toConfigurerFunction
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic


/**
 * 构建一个 [Application] 并启动它。
 *
 * ```kotlin
 * val app = launchApplication(Simple) {
 *     // ...
 * }
 * ```
 *
 */
@JvmSynthetic
public suspend inline fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplication(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    crossinline configurer: ApplicationFactoryConfigurer<C, AER, DC>.() -> Unit = {}
): A {
    return factory.create(toConfigurerFunction(configurer)).launch()
}

/**
 * 构建一个 [Application] 并异步地启动它。
 */
@JvmOverloads
public fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplicationAsync(
    scope: CoroutineScope,
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): Async<A> {
    val launcher = runCatching {
        factory.create(configurer?.let { c ->
            toConfigurerFunction {
                c.invokeWith(this)
            }
        })
    }.getOrElse { e ->
        return CompletableDeferred<A>().apply {
            completeExceptionally(e)
        }.toAsync()
    }

    return scope.toAsync { launcher.launch() }
}


/**
 * 构建一个 [Application] 并异步地启动它。
 */
@OptIn(DelicateCoroutinesApi::class)
@JvmOverloads
public fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplicationAsync(
    factory: ApplicationFactory<A, C, L, AER, DC>,
    configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>? = null
): Async<A> = launchApplicationAsync(scope = GlobalScope, factory = factory, configurer = configurer)

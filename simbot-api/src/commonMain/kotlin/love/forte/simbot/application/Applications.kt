package love.forte.simbot.application

import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.function.toConfigurerFunction


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
public suspend inline fun <A : Application, C : AbstractApplicationBuilder, L : ApplicationLauncher<A>, AER : ApplicationEventRegistrar, DC : EventDispatcherConfiguration> launchApplication(
    factory: ApplicationFactory<A, C, L, AER, DC>, crossinline configurer: ApplicationFactoryConfigurer<C, AER, DC>.() -> Unit
): A {
    return factory.create(toConfigurerFunction(configurer)).launch()
}

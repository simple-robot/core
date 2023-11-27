package love.forte.simbot.core.application

import love.forte.simbot.application.*
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration

/**
 * 通过 [Simple] 工厂构建可得的 [Application] 实现类型。
 *
 */
public interface SimpleApplication : Application {
    override val configuration: SimpleApplicationConfiguration
}

/**
 * 使用 [Simple] 构建 [SimpleApplication] 时与之对应的 [ApplicationConfiguration] 类型扩展。
 *
 */
public interface SimpleApplicationConfiguration : ApplicationConfiguration {
    // properties?
}

/**
 * 针对 [Simple] 工厂构建 [SimpleApplication] 的 [ApplicationLauncher] 实现。
 */
public interface SimpleApplicationLauncher : ApplicationLauncher<SimpleApplication>


/**
 * 构建一个 [SimpleApplication] 并启动它。
 *
 * ```kotlin
 * val app = launchSimpleApplication {
 *     // ...
 * }
 * ```
 *
 */
public suspend inline fun launchSimpleApplication(
    crossinline configurer: ApplicationFactoryConfigurer<SimpleApplicationBuilder, ApplicationEventRegistrar, SimpleEventDispatcherConfiguration>.() -> Unit
): SimpleApplication {
    return launchApplication(Simple, configurer)
}

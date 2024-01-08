package love.forte.simbot.spring.application

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.application.*
import love.forte.simbot.core.event.SimpleEventDispatcherConfiguration
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * 使用在 Spring Starter 中的 [Application] 实现。
 * 主要由内部使用。
 */
public interface SpringApplication : Application

/**
 * Factory for [SpringApplication].
 */
public interface SpringApplicationFactory :
    ApplicationFactory<SpringApplication, SpringApplicationBuilder, SpringApplicationLauncher, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationBuilder]
 *
 */
public open class SpringApplicationBuilder : AbstractApplicationBuilder() {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public open var applicationConfigurationProperties: SpringApplicationConfigurationProperties =
        SpringApplicationConfigurationProperties()

    /**
     * Build [SpringApplicationConfiguration]
     */
    @InternalSimbotAPI
    public open fun build(): SpringApplicationConfiguration =
        SpringApplicationConfigurationImpl(
            coroutineContext,
            applicationConfigurationProperties
        )

}

public interface SpringApplicationConfiguration : ApplicationConfiguration {
    /**
     * @see SpringApplicationConfigurationProperties
     */
    public val applicationConfigurationProperties: SpringApplicationConfigurationProperties
}

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationLauncher] 实现。
 *
 */
public interface SpringApplicationLauncher : ApplicationLauncher<SpringApplication>

/**
 * Simbot Spring Boot Starter 中使用的 [ApplicationEventRegistrar] 实现。
 *
 */
public interface SpringApplicationEventRegistrar : ApplicationEventRegistrar

/**
 * Simbot Spring Boot Starter 中使用的调度器配置。
 *
 */
public interface SpringEventDispatcherConfiguration : SimpleEventDispatcherConfiguration {

    /**
     * 添加一个 [Executor] 并作为 [协程调度器][CoroutineDispatcher] 添加到 [CoroutineContext] 中。
     */
    public fun setExecutorDispatcher(executor: Executor) {
        coroutineContext += executor.asCoroutineDispatcher()
    }
}

/**
 * Implementation for [SpringApplicationConfiguration]
 */
@InternalSimbotAPI
public open class SpringApplicationConfigurationImpl(
    override val coroutineContext: CoroutineContext,
    override val applicationConfigurationProperties: SpringApplicationConfigurationProperties,
) : SpringApplicationConfiguration

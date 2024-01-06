package love.forte.simbot.spring.configuration

import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.spring.application.*


/**
 * 在 simbot 的 starter 中对
 * [Spring] 进行实际配置处理并得到
 * [SpringApplicationLauncher] 的处理器。
 * 只能存在一个，当用户自定义时会覆盖**全部**的默认行为。
 *
 * @see DefaultSimbotSpringApplicationLauncherFactory
 *
 * @author ForteScarlet
 */
public interface SimbotSpringApplicationLauncherFactory {

    /**
     * 根据 [factory] 进行处理并得到 [SpringApplicationLauncher].
     */
    public fun process(factory: Spring): SpringApplicationLauncher

}

/**
 * 在 [SimbotSpringApplicationLauncherFactory] 的 **默认** 情况下,
 * 会加载 [SimbotSpringApplicationLauncherPreConfigurer] 和 [SimbotSpringApplicationLauncherPostConfigurer]
 * 分别在默认加载行为之前和之后插入可自定义的配置逻辑。
 *
 * 如果 [SimbotSpringApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotSpringApplicationLauncherFactory
 * @see DefaultSimbotSpringApplicationLauncherFactory
 * @see SimbotSpringApplicationLauncherPreConfigurer
 * @see SimbotSpringApplicationLauncherPostConfigurer
 */
public sealed interface SimbotSpringApplicationLauncherConfigurer {

    /**
     * 配置 [SpringApplicationBuilder].
     */
    public fun configure(configurer: ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>)

}

/**
 * 在 [SimbotSpringApplicationLauncherFactory] 的 **默认** 情况下,
 * 会加载 [SimbotSpringApplicationLauncherPreConfigurer] 和 [SimbotSpringApplicationLauncherPostConfigurer]
 * 会在默认加载行为之前插入可自定义的配置逻辑。
 *
 * 如果 [SimbotSpringApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotSpringApplicationLauncherFactory
 * @see DefaultSimbotSpringApplicationLauncherFactory
 * @see SimbotSpringApplicationLauncherConfigurer
 * @see SimbotSpringApplicationLauncherPostConfigurer
 */
public interface SimbotSpringApplicationLauncherPreConfigurer :
    SimbotSpringApplicationLauncherConfigurer

/**
 * 在 [SimbotSpringApplicationLauncherFactory] 的 **默认** 情况下,
 * 会在默认加载行为之后插入可自定义的配置逻辑。
 *
 * 如果 [SimbotSpringApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotSpringApplicationLauncherFactory
 * @see DefaultSimbotSpringApplicationLauncherFactory
 * @see SimbotSpringApplicationLauncherConfigurer
 * @see SimbotSpringApplicationLauncherPreConfigurer
 */
public interface SimbotSpringApplicationLauncherPostConfigurer :
    SimbotSpringApplicationLauncherConfigurer




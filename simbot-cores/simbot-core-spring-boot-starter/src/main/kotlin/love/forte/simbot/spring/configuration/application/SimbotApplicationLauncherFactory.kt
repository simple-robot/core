package love.forte.simbot.spring.configuration.application

import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.spring.application.*


/**
 * 在 simbot 的 starter 中对
 * [Spring] 进行实际配置处理并得到
 * [SpringApplicationLauncher] 的处理器。
 * 只能存在一个，当用户自定义时会覆盖**全部**的默认行为。
 *
 * @see DefaultSimbotApplicationLauncherFactory
 *
 * @author ForteScarlet
 */
public interface SimbotApplicationLauncherFactory {

    /**
     * 根据 [factory] 进行处理并得到 [SpringApplicationLauncher].
     */
    public fun process(factory: Spring): SpringApplicationLauncher

}

/**
 * 在 [SimbotApplicationLauncherFactory] 的 **默认** 情况下,
 * 会加载 [SimbotApplicationLauncherPreConfigurer] 和 [SimbotApplicationLauncherPostConfigurer]
 * 分别在默认加载行为之前和之后插入可自定义的配置逻辑。
 *
 * 如果 [SimbotApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotApplicationLauncherFactory
 * @see DefaultSimbotApplicationLauncherFactory
 * @see SimbotApplicationLauncherPreConfigurer
 * @see SimbotApplicationLauncherPostConfigurer
 */
public sealed interface SimbotApplicationLauncherConfigurer {

    /**
     * 配置 [SpringApplicationBuilder].
     */
    public fun configure(configurer: ApplicationFactoryConfigurer<SpringApplicationBuilder, SpringApplicationEventRegistrar, SpringEventDispatcherConfiguration>)

}

/**
 * 在 [SimbotApplicationLauncherFactory] 的 **默认** 情况下,
 * 会加载 [SimbotApplicationLauncherPreConfigurer] 和 [SimbotApplicationLauncherPostConfigurer]
 * 会在默认加载行为之前插入可自定义的配置逻辑。
 *
 * 如果 [SimbotApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotApplicationLauncherFactory
 * @see DefaultSimbotApplicationLauncherFactory
 * @see SimbotApplicationLauncherConfigurer
 * @see SimbotApplicationLauncherPostConfigurer
 */
public interface SimbotApplicationLauncherPreConfigurer :
    SimbotApplicationLauncherConfigurer

/**
 * 在 [SimbotApplicationLauncherFactory] 的 **默认** 情况下,
 * 会在默认加载行为之后插入可自定义的配置逻辑。
 *
 * 如果 [SimbotApplicationLauncherFactory] 的默认行为被覆盖则需要自行处理，
 * 否则不会生效。
 *
 * @see SimbotApplicationLauncherFactory
 * @see DefaultSimbotApplicationLauncherFactory
 * @see SimbotApplicationLauncherConfigurer
 * @see SimbotApplicationLauncherPreConfigurer
 */
public interface SimbotApplicationLauncherPostConfigurer :
    SimbotApplicationLauncherConfigurer




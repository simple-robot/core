package love.forte.simbot.spring.configuration.application

import love.forte.simbot.spring.application.SpringApplication
import love.forte.simbot.spring.application.SpringApplicationLauncher


/**
 * 用于在 [SpringApplicationLauncher] 启动完成得到 [SpringApplication]
 * 后进行处理的处理器。
 *
 * [SimbotApplicationProcessor] 默认使用 [DefaultSimbotApplicationProcessor]，
 * 可提供自定义类型覆盖默认行为。
 * 但是注意！覆盖 [SimbotApplicationProcessor] 会导致**所有**默认行为失效，
 * 例如加载事件处理器、自动加载、注册 bot 并保持整个程序活跃等。
 *
 * 如果希望在启动完成得到 [SpringApplication] 并进行默认行为前后执行某些逻辑，
 * 可考虑使用 [SimbotApplicationConfigurer]。
 *
 *
 */
public interface SimbotApplicationProcessor {
    /**
     * 处理 [SpringApplication].
     */
    public fun process(application: SpringApplication)

}

/**
 * 在 [DefaultSimbotApplicationProcessor]
 * 中被加载并对 [SpringApplication] 进行配置的配置接口，
 * 使用 [SimbotApplicationPreConfigurer] 和 [SimbotApplicationPostConfigurer]
 * 分别代表在进行默认行为之前或之后进行的配置逻辑。
 *
 * 可以注册多个。
 *
 * @see SimbotApplicationPreConfigurer
 * @see SimbotApplicationPostConfigurer
 *
 */
public sealed interface SimbotApplicationConfigurer {
    /**
     * 使用 [application]
     */
    public fun configure(application: SpringApplication)
}

/**
 * 在 [DefaultSimbotApplicationProcessor] 进行默认行为之前进行的配置逻辑。
 * @see SimbotApplicationConfigurer
 */
public fun interface SimbotApplicationPreConfigurer : SimbotApplicationConfigurer

/**
 * 在 [DefaultSimbotApplicationProcessor] 进行默认行为之后进行的配置逻辑。
 * @see SimbotApplicationConfigurer
 */
public fun interface SimbotApplicationPostConfigurer : SimbotApplicationConfigurer



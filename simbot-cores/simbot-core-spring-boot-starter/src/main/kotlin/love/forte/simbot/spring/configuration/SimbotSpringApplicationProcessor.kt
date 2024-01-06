package love.forte.simbot.spring.configuration

import love.forte.simbot.spring.application.SpringApplication
import love.forte.simbot.spring.application.SpringApplicationLauncher


/**
 * 用于在 [SpringApplicationLauncher] 启动完成得到 [SpringApplication]
 * 后进行处理的处理器。
 *
 * [SimbotSpringApplicationProcessor] 默认使用 [DefaultSimbotSpringApplicationProcessor]，
 * 可提供自定义类型覆盖默认行为。
 * 但是注意！覆盖 [SimbotSpringApplicationProcessor] 会导致**所有**默认行为失效，
 * 例如加载事件处理器、自动加载、注册 bot 并保持整个程序活跃等。
 *
 * 如果希望在启动完成得到 [SpringApplication] 并进行默认行为前后执行某些逻辑，
 * 可考虑使用 [SimbotSpringApplicationConfigurer]。
 *
 *
 */
public interface SimbotSpringApplicationProcessor {
    /**
     * 处理 [SpringApplication].
     */
    public fun process(application: SpringApplication)

}

/**
 * 在 [DefaultSimbotSpringApplicationProcessor]
 * 中被加载并对 [SpringApplication] 进行配置的配置接口，
 * 使用 [SimbotSpringApplicationPreConfigurer] 和 [SimbotSpringApplicationPostConfigurer]
 * 分别代表在进行默认行为之前或之后进行的配置逻辑。
 *
 * 可以注册多个。
 *
 * @see SimbotSpringApplicationPreConfigurer
 * @see SimbotSpringApplicationPostConfigurer
 *
 */
public sealed interface SimbotSpringApplicationConfigurer {
    /**
     * 使用 [application]
     */
    public fun configure(application: SpringApplication)
}

/**
 * 在 [DefaultSimbotSpringApplicationProcessor] 进行默认行为之前进行的配置逻辑。
 * @see SimbotSpringApplicationConfigurer
 */
public interface SimbotSpringApplicationPreConfigurer : SimbotSpringApplicationConfigurer

/**
 * 在 [DefaultSimbotSpringApplicationProcessor] 进行默认行为之后进行的配置逻辑。
 * @see SimbotSpringApplicationConfigurer
 */
public interface SimbotSpringApplicationPostConfigurer : SimbotSpringApplicationConfigurer

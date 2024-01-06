package love.forte.simbot.spring.application

import love.forte.simbot.application.Application
import love.forte.simbot.bot.Bot


/**
 * 通过 Spring Boot 配置文件进行配置的各项属性。
 *
 * @author ForteScarlet
 */
public class SpringApplicationConfigurationProperties {
    /**
     * 需要加载的所有组件下它们对应的所有bot配置文件。
     * 键为对应组件的 id，值为对应的资源列表。
     * 资源文件的内容应为 JSON 格式。
     */
    public var botConfigurationResources: Map<String, List<String>> = emptyMap()

    /**
     * 是否在 `Bot` 注册后使用 [Bot.start] 启动它们。
     *
     */
    public var isAutoStartBots: Boolean = true

    /**
     * 当自动扫描的bot注册或启动失败时的处理策略。默认为直接抛出异常。
     */
    public var botAutoRegistrationFailurePolicy: BotRegistrationFailurePolicy = BotRegistrationFailurePolicy.ERROR

    /**
     * 组件相关的配置信息。
     */
    public var components: ComponentProperties = ComponentProperties()

    /**
     * 组件相关的配置信息。
     */
    public class ComponentProperties {
        /**
         * 是否通过 SPI 自动加载所有可寻得的组件
         */
        public var autoInstallProviders: Boolean = true

        /**
         * 是否在加载 SPI providers 时候也同时加载它们的前置配置。
         * [autoInstallProviders] 为 `true` 时有效。
         */
        public var autoInstallProviderConfigurers: Boolean = true
    }

    /**
     * 插件相关的配置信息。
     */
    public var plugins: PluginProperties = PluginProperties()

    /**
     * 插件相关的配置信息。
     */
    public class PluginProperties {
        /**
         * 是否通过 SPI 自动加载所有可寻得的插件
         */
        public var autoInstallProviders: Boolean = true

        /**
         * 是否在加载 SPI providers 时候也同时加载它们的前置配置。
         * [autoInstallProviders] 为 `true` 时有效。
         */
        public var autoInstallProviderConfigurers: Boolean = true
    }

    /**
     * 与 [Application] 相关的配置。
     */
    public var application: ApplicationProperties = ApplicationProperties()

    /**
     * 与 [Application] 相关的配置。
     */
    public class ApplicationProperties {
        /**
         * 保持 [Application] （或者说整个程序活跃）的策略。默认为 [ApplicationLaunchMode.NONE]
         */
        public var applicationLaunchMode: ApplicationLaunchMode = ApplicationLaunchMode.NONE

    }
}

/**
 * 基于 [Application] 保活的策略
 */
public enum class ApplicationLaunchMode {
    /**
     * 使用一个独立的非守护线程来保持程序活跃。
     */
    THREAD,

    /**
     * 不进行任何行为，适用于环境中有其他可保证程序运行的内容，
     * 例如 spring-web。是默认选项
     */
    NONE
}


/**
 * 当自动扫描的bot注册或启动失败时的处理策略。
 */
public enum class BotRegistrationFailurePolicy {

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时都会抛出异常并终止程序。
     *
     * 是默认的选项。
     */
    ERROR,

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时会输出带有异常信息的 `warn` 日志。
     */
    WARN,

    /**
     * 当bot注册或启动过程中出现异常或bot最终无法注册时仅会输出 `debug` 调试日志。
     */
    IGNORE;
}

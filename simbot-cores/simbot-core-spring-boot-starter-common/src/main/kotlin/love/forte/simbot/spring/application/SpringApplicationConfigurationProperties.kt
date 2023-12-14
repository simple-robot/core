package love.forte.simbot.spring.application

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

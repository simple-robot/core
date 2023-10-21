package love.forte.simbot.annotations

/**
 * 一个尚在试验阶段的API。试验阶段的API可能存在漏洞、缺陷，或实现不稳定，
 * 且有可能在未来被修改、删除，且没有兼容性保证。
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个尚在试验阶段的API。试验阶段的API可能存在漏洞、缺陷，或实现不稳定，且有可能在未来被修改、删除，且没有兼容性保证。",
    level = RequiresOptIn.Level.WARNING
)
public annotation class ExperimentalAPI

/**
 * 一个仅供 simbot 内部使用的API。
 * 它可能会随时变更、删除，且不保证兼容性。
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个仅供 simbot 内部使用的API。它可能会随时变更、删除，且不保证兼容性。",
    level = RequiresOptIn.Level.WARNING
)
public annotation class InternalAPI

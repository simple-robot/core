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
@MustBeDocumented
public annotation class ExperimentalSimbotAPI

/**
 * 一个仅供 simbot 内部使用的API。
 * 它可能会随时变更、删除，且不保证兼容性。
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个仅供 simbot 内部使用的API。它可能会随时变更、删除，且不保证兼容性。",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class InternalSimbotAPI

/**
 * 一个设计为仅供 Java 用户使用的API
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个设计为仅供 Java 用户使用的API",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class Api4J

/**
 * 一个设计为仅供 JS 用户使用的API
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "一个设计为仅供 JS 用户使用的API",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
public annotation class Api4Js

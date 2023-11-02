package love.forte.simbot.application

import kotlinx.coroutines.Job
import love.forte.simbot.utils.linkTo
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 一个 [Application] 所需的最基础的配置信息内容。
 *
 * 针对不同 [Application] 的实现可以自由扩展 [ApplicationConfiguration]，
 * 但是应当至少其要求的属性与能力，最少也应在标准属性不支持的情况下提供警告日志或异常。
 *
 */
public interface ApplicationConfiguration {
    /**
     * [Application] 中的协程上下文。
     *
     * [Application] 本身存在生命周期，如果 [coroutineContext] 中存在 [Job],
     * 则会被作为父任务被关联。
     *
     * [Application] 中的 [coroutineContext] 会在此配置中被传递给其他子配置（例如 [Plugins] 或 [Components]），
     * 而是否会使用此上下文则交由它们自行决定（[Application] 也无法干涉）。
     * 我们建议使用 [Application] 的 [coroutineContext] 作为各子配置的基础上下文，
     * 至少将生命周期与 [Application] 进行关联（使用父子任务或在存在多个任务的情况下使用 [Job.linkTo] 关联到 [Application] 的任务上），
     * 由此来保证 [Application] 生命周期的影响和有效性。
     *
     */
    public val coroutineContext: CoroutineContext

}

/**
 * 用于构建 [ApplicationConfiguration] 的基础实现，提供针对 [ApplicationConfiguration] 基础属性的配置能力。
 * 也可用于其他实现者进行扩展。
 *
 */
public open class ApplicationConfigurationBuilder {

    /**
     * [ApplicationConfiguration.coroutineContext] 配置属性，默认为 [EmptyCoroutineContext]。
     *
     * @see ApplicationConfiguration.coroutineContext
     */
    public open var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 构建并得到一个 [ApplicationConfiguration] 结果。
     */
    public open fun build(): ApplicationConfiguration = Impl(coroutineContext)


    private data class Impl(override val coroutineContext: CoroutineContext) : ApplicationConfiguration
}

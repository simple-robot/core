@file:JvmMultifileClass
@file:JvmName("Applications")

package love.forte.simbot.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import love.forte.simbot.component.Component
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.utils.toImmutable
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 一个 simbot application.
 * [Application] 可以代表为一个或一组组件、插件在一起运行的单位。
 *
 * @author ForteScarlet
 */
public interface Application : CoroutineScope {
    /**
     * 构建 [Application] 提供并得到的最终配置信息。
     */
    public val configuration: ApplicationConfiguration

    /**
     * [Application] 作为一个协程作用域的上下文信息。
     * 应当必然包含一个描述生命周期的任务 [Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 当前 [Application] 持有的事件调度器。
     */
    public val eventDispatcher: EventDispatcher

    /**
     * 当前 [Application] 中注册的所有组件集。
     */
    public val components: Components

    /**
     * 当前 [Application] 中注册的所有插件集。
     */
    public val plugins: Plugins

    /**
     * 申请关闭当前 [Application]。
     *
     * 在真正关闭 [coroutineContext] 中的 [Job] 之前，
     * 会通过 [ApplicationLaunchStage.Cancelled] 触发
     */
    public fun cancel()

    /**
     * 挂起 [Application] 直到调用 [cancel] 且其内部完成了关闭 Job 的操作后。
     */
    @JvmSynthetic
    public suspend fun join() {
        coroutineContext[Job]?.join()
    }
}

//region Components
/**
 * 用于表示一组 components.
 */
public interface Components : Collection<Component>

/**
 * 根据类型寻找某个 [Component]。
 */
public inline fun <reified C : Component> Components.find(): C? = find { it is C } as C?

/**
 * 根据类型寻找某个 [Component]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified C : Component> Components.get(): C =
    find<C>() ?: throw NoSuchElementException(C::class.toString())


/**
 * 将一个 [Component] 的集合转化为 [Components]。
 */
public fun Collection<Component>.toComponents(): Components = CollectionComponents(toImmutable())

/**
 * @see Components
 */
private class CollectionComponents(private val collections: Collection<Component>) : Components,
    Collection<Component> by collections {
    override fun toString(): String = "Components(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionComponents) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }


}
//endregion

//region Plugins
/**
 * 用于表示一组 plugins.
 */
public interface Plugins : Collection<Plugin>

/**
 * 根据类型寻找某个 [Plugin]。
 */
public inline fun <reified P : Plugin> Plugins.find(): P? = find { it is P } as P?

/**
 * 根据类型寻找某个 [Plugin]，如果找不到则抛出 [NoSuchElementException]。
 *
 * @throws NoSuchElementException 如果没找到匹配的类型
 */
public inline fun <reified P : Plugin> Plugins.get(): P = find<P>() ?: throw NoSuchElementException(P::class.toString())

/**
 * 将一个 [Plugin] 的集合转化为 [Plugins]。
 */
public fun Collection<Plugin>.toPlugins(): Plugins = CollectionPlugins(toImmutable())

/**
 * @see Plugins
 */
private class CollectionPlugins(private val collections: Collection<Plugin>) : Plugins,
    Collection<Plugin> by collections {
    override fun toString(): String = "Plugins(values=$collections)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionPlugins) return false

        if (collections != other.collections) return false

        return true
    }

    override fun hashCode(): Int {
        return collections.hashCode()
    }

}
//endregion



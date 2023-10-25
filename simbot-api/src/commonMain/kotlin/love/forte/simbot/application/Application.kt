package love.forte.simbot.application

import love.forte.simbot.component.Component
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.utils.toImmutable

/**
 * 一个 simbot application.
 * [Application] 可以代表为一个或一组组件、插件在一起运行的单位。
 *
 * @author ForteScarlet
 */
public interface Application {
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

}

/**
 * 用于表示一组 components.
 */
public interface Components : Collection<Component>

/**
 * 将一个 [Component] 的集合转化为 [Components]。
 */
public fun Collection<Component>.toComponents(): Components = CollectionComponents(toImmutable())

private class CollectionComponents(private val collections: Collection<Component>) : Components,
    Collection<Component> by collections


/**
 * 用于表示一组 plugins.
 */
public interface Plugins : Collection<Plugin>

/**
 * 将一个 [Plugin] 的集合转化为 [Plugins]。
 */
public fun Collection<Plugin>.toPlugins(): Plugins = CollectionPlugins(toImmutable())

private class CollectionPlugins(private val collections: Collection<Plugin>) : Plugins,
    Collection<Plugin> by collections

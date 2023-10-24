package love.forte.simbot.event


/**
 * 事件监听函数的注册器。
 * 用于承载注册、管理监听函数的职责。
 *
 * @author ForteScarlet
 */
public interface EventListenerRegistrar {
    // register
}

/**
 * 注册事件监听函数的额外选项。
 */
public interface RegisterOptions {
    // TODO 一个配置类型？还是多个配置散件儿？

}

// TODO Registered Handle
public interface EventListenerHandle {
    public fun dispose()
}

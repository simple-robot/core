package love.forte.simbot.spring.configuration.binder

import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory

/**
 * 如果注册 [ParameterBinderFactory] 时 id 出现重复
 */
public open class DuplicateBinderIdException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

/**
 * 应用于 [ParameterBinderManagerBuilderConfigurer] 的构建器。
 *
 */
public interface ParameterBinderManagerBuilder {
    /**
     * 添加一个有 ID 的具体作用域b [ParameterBinderFactory]
     *
     * @throws DuplicateBinderIdException 如果 id 出现重复
     */
    public fun addBinderFactory(id: String, factory: ParameterBinderFactory)

    /**
     * 添加一个全局应用的 [ParameterBinderFactory]
     */
    public fun addBinderFactory(factory: ParameterBinderFactory)
}

/**
 * 在默认行为中会被 [DefaultBinderManagerProvider] 批量加载并配置。
 */
public fun interface ParameterBinderManagerBuilderConfigurer {
    /**
     * 配置 [builder]
     */
    public fun configure(builder: ParameterBinderManagerBuilder)
}

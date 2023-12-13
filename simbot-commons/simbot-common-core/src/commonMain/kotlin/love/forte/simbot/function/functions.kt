package love.forte.simbot.function


// fun interface in JS?

/**
 * Represents an action that can be performed on a value of type T.
 *
 * @param T the type of the value that the action operates on
 */
public fun interface Action<in T> {
    /**
     * Invokes the operator function with the given value.
     *
     * @param value the value to be used in the operator function
     */
    public operator fun invoke(value: T)
}



/**
 * 一个"配置"函数接口。通过 `receiver` 接收配置信息参数。
 *
 * 类似于 `CONF.() -> Unit` 。
 *
 * @author ForteScarlet
 */
public fun interface ConfigurerFunction<in CONF> {
    /**
     * 配置逻辑。
     */
    public operator fun CONF.invoke()
}

/**
 * `CONF.() -> Unit` to [ConfigurerFunction]。
 */
public inline fun <CONF> toConfigurerFunction(crossinline block: CONF.() -> Unit): ConfigurerFunction<CONF> {
    return ConfigurerFunction {
        block()
    }
}

/**
 * Invoke [ConfigurerFunction] with [conf]。
 */
public fun <CONF> ConfigurerFunction<CONF>.invokeWith(conf: CONF) {
    conf.apply { invoke() }
}

/**
 * Invoke [configurer] with [CONF]。
 */
public fun <CONF> CONF.invokeBy(configurer: ConfigurerFunction<CONF>?): CONF {
    return this.also { configurer?.invokeWith(it) }
}

/**
 * Merge double [ConfigurerFunction]
 *
 */
public operator fun <CONF> ConfigurerFunction<CONF>.plus(other: ConfigurerFunction<CONF>): ConfigurerFunction<CONF> {
    val old = this
    return ConfigurerFunction {
        val value = this
        old.invokeWith(value)
        other.invokeWith(value)
    }
}


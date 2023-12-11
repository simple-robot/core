package love.forte.simbot.function


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


public operator fun <CONF> ConfigurerFunction<CONF>.plus(other: ConfigurerFunction<CONF>): ConfigurerFunction<CONF> {
    val old = this
    return ConfigurerFunction {
        val value = this
        old.invokeWith(value)
        other.invokeWith(value)
    }
}

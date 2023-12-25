package love.forte.simbot.bot.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 得到一个与 [Ktor proxy](https://ktor.io/docs/proxy.html#http_proxy) 类似的配置结果的配置类。
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class ProxyConfiguration {
    /**
     * 得到配置结果
     */
    public abstract val value: ProxyValue

    /**
     * Http 代理
     */
    @Serializable
    @SerialName("http")
    public data class Http(val url: String) : ProxyConfiguration() {
        override val value: ProxyValue.Http
            get() = ProxyValue.Http(url)
    }

    /**
     * socks 代理
     */
    @Serializable
    @SerialName("socks")
    public data class Socks(val host: String, val port: Int) : ProxyConfiguration() {
        override val value: ProxyValue.Socks
            get() = ProxyValue.Socks(host, port)
    }
}


/**
 * [ProxyConfiguration] 的配置结果类型。
 *
 * @see ProxyConfiguration
 */
public sealed class ProxyValue {
    /**
     * HTTP proxy
     */
    public data class Http(val url: String) : ProxyValue()

    /**
     * socks proxy.
     */
    public data class Socks(val host: String, val port: Int) : ProxyValue()
}

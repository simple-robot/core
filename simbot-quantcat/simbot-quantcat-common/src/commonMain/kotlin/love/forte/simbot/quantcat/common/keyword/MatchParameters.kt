package love.forte.simbot.quantcat.common.keyword

/**
 * 匹配器动态参数获取器
 * @author ForteScarlet
 */
public interface MatchParameters {

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key Key
     * @return Value or null.
     */
    public operator fun get(key: String): String?
}

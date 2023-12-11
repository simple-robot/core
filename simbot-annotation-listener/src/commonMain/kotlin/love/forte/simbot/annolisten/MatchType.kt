package love.forte.simbot.annolisten


/**
 * 关键词匹配类型。
 *
 * @author ForteScarlet
 */
public enum class MatchType(private val matcher: KeywordMatcher) {

    /**
     * 全等匹配
     *
     * @see String.equals
     */
    TEXT_EQUALS({ keyword, value -> keyword.text == value }),

    /**
     * 忽略大小写的全等匹配
     *
     * @see String.equals
     *
     */
    TEXT_EQUALS_IGNORE_CASE({ keyword, value -> value.equals(keyword.text, ignoreCase = true) }),

    /**
     * 首部匹配
     *
     * @see String.startsWith
     */
    TEXT_STARTS_WITH({ keyword, value -> value.startsWith(keyword.text) }),

    /**
     * 尾部匹配.
     *
     * @see String.endsWith
     */
    TEXT_ENDS_WITH({ keyword, value -> value.endsWith(keyword.text) }),

    /**
     * 包含匹配.
     *
     * @see String.contains
     */
    TEXT_CONTAINS({ keyword, value -> keyword.text in value }),

    /**
     * 正则完全匹配. `regex.matches(...)`
     *
     * @see Regex.matches
     */
    REGEX_MATCHES({ keyword, value -> keyword.regex.matches(value) }),

    /**
     * 正则包含匹配. `regex.containsMatchIn(...)`
     *
     * @see Regex.containsMatchIn
     *
     */
    REGEX_CONTAINS({ keyword, value -> keyword.regex.containsMatchIn(value) });

    /**
     * 提供一个匹配关键词 [keyword] 和匹配目标 [value], 对其进行匹配并返回匹配结果。
     *
     * @return [value] 是否与预期关键词 [keyword] 匹配
     */
    public fun match(keyword: Keyword, value: String): Boolean = matcher.test(keyword, value)

}

/**
 * 基于关键词 [Keyword] 的字符串匹配器接口。
 *
 * @author ForteScarlet
 */
public fun interface KeywordMatcher {
    /**
     * 提供一个匹配关键词 [keyword] 和匹配目标 [value], 对其进行匹配并返回匹配结果。
     *
     * @return [value] 是否与预期关键词 [keyword] 匹配
     */
    public fun test(keyword: Keyword, value: String): Boolean

}

/**
 * 一组用于匹配的关键词。
 *
 * @see KeywordMatcher
 * @author ForteScarlet
 */
public interface Keyword {
    /**
     * 关键词的字符串文本。
     */
    public val text: String

    /**
     * 关键词文本 [text] 转化后的正则类型。
     */
    public val regex: Regex
}


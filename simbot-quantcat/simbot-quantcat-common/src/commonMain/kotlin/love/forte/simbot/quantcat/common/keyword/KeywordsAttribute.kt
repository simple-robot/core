package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.attribute
import love.forte.simbot.quantcat.common.filter.MatchType

/**
 * 装载 [Keyword] 列表的属性key。
 */
public val KeywordListAttribute: Attribute<MutableList<Keyword>> = attribute("\$listener.keywordList")

/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
internal class SimpleKeyword(override val text: String, matchType: MatchType) : Keyword {
    override val valueMatcher: ValueMatcher
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexValueMatcher(text, matchType.isPlainText)
        valueMatcher = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}


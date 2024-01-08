package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.attribute

/**
 * 装载 [Keyword] 列表的属性key。
 */
public val KeywordListAttribute: Attribute<MutableList<Keyword>> = attribute("\$listener.keywordList")

/**
 * 一个普通的value值构建为 [Keyword] 实例。
 */
public class SimpleKeyword(override val text: String, isPlainText: Boolean = false) : Keyword {
    override val regexValueMatcher: ValueMatcher
    override val regex: Regex

    init {
        val regexParameterMatcher = RegexValueMatcher(text, isPlainText)
        regexValueMatcher = regexParameterMatcher
        regex = regexParameterMatcher.regex
    }
}


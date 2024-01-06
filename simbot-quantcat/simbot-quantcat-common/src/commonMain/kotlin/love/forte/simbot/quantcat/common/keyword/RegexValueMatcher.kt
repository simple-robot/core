package love.forte.simbot.quantcat.common.keyword

/**
 * 正则参数提取器。
 */
public class RegexValueMatcher(private val originalValue: String, private val isPlainText: Boolean) : ValueMatcher {


    public override val regex: Regex by lazy(LazyThreadSafetyMode.PUBLICATION) {
        if (isPlainText) {
            Regex.fromLiteral(originalValue)
        } else {
            val regexValue = originalValue.toDynamicParametersRegexValue()
            Regex(regexValue)
        }
    }

    /**
     * 获取原始字符串
     * @return 原始字符串
     */
    override val original: String get() = originalValue

    /**
     * 完全正则匹配。
     */
    override fun matches(text: String): Boolean = regex.matches(text)

    /**
     * 根据变量名称获取一个动态参数。
     * 此文本需要符合正则表达式。
     * @param name 变量名称
     * @param text 文本
     * @return 得到的参数
     */
    override fun getParam(name: String, text: String): String? {
        return getParameters(text)[name]
    }

    /**
     * 从一段匹配的文本中提取出需要的参数。
     *
     * 此文本需要符合正则表达式, 否则得到null。
     *
     * @param text 匹配文本
     * @return 参数提取器。
     */
    override fun getParameters(text: String?): MatchParameters {

        return text?.let { t -> regex.matchEntire(t)?.let { result -> MatcherParameters(result) } }
            ?: EmptyFilterParameters
    }
}

/**
 * [MatchResult] 对应的匹配值Map。
 */
private class MatcherParameters(matcher: MatchResult) : MatchParameters {
    private val groups = matcher.groups

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key
     * @return
     */
    override fun get(key: String): String? = groups[key]?.value
}

/**
 * 将一个可能携带动态参数的正则字符串转化为普通的正则字符串。
 *
 * e.g.
 * `xxx{{name,\\d+}}` -> `xxx(?<name>\\d+)`
 *
 *
 */
private fun String.toDynamicParametersRegexValue(): String {
    val builder = StringBuilder(length)
    val temp = StringBuilder(16)
    // 忽略本次（上次为转义符
    // var ignoreThis = false
    // 正在获取
    var on = false
    val iter = this.iterator()

    var flag = 0

    while (iter.hasNext()) {
        val c = iter.nextChar()
        if (!on) {
            // NOT ON

            if (c == '{') {
                // maybe first
                // if next also
                if (iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next == '{') {
                        on = true
                    } else {
                        builder.append(c).append(next)
                    }
                } else {
                    // not first, is the end. just append
                    builder.append(c)
                }
            } else {
                builder.append(c)
            }
        } else {
            // ON
            if (c == '{') {
                flag++
                temp.append(c)
            } else if (c == '}') {
                // if next also
                if (flag == 0 && iter.hasNext()) {
                    val next = iter.nextChar()
                    if (next == '}') {
                        builder.append(temp.toString().dynamicParametersToRegex())
                        temp.clear()
                        on = false
                    } else {
                        temp.append(c).append(next)
                    }
                } else {
                    flag--
                    // no next, the end. just append
                    temp.append(c)
                }
            } else {
                temp.append(c)
            }

        }
    }


    if (on) {
        throw IllegalStateException("There is no end flag '}}' for dynamic parameters.")
    }


    return builder.toString()
}


/**
 * 将动态参数中的内容转化为正则分组的格式。
 * `name[,regex]` -> `(?<name>regex)`
 */
private fun String.dynamicParametersToRegex(): String {
    val sp = split(',', limit = 2)
    val name: String = sp[0]
    val regex: String = sp.takeIf { s -> s.size > 1 }?.get(1) ?: ".+"

    return "(?<$name>$regex)"

}

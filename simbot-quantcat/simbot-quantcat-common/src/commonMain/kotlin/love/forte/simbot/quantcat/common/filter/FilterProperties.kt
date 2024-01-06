package love.forte.simbot.quantcat.common.filter


/**
 * 参考注解 `@Filter` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterProperties(
    public val value: String,
    public val mode: FilterMode,
    public val priority: Int,
    public val targets: List<FilterTargetsProperties>,
    public val ifNullPass: Boolean,
    public val matchType: MatchType,
)

/**
 * 参考注解 `@Filter.Targets` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterTargetsProperties(
    val components: List<String>,
    val bots: List<String>,
    val actors: List<String>,
    val authors: List<String>,
    val chatRooms: List<String>,
    val organizations: List<String>,
    val groups: List<String>,
    val guilds: List<String>,
    val contacts: List<String>,
    val ats: List<String>,
    val atBot: Boolean,
)
/**
 * 参考注解 `@FilterValue` 中的属性说明。
 *
 * @author ForteScarlet
 */
public data class FilterValueProperties(
    val value: String,
    val required: Boolean = true
)

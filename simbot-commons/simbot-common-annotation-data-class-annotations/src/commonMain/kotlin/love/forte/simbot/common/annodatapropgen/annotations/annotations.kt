package love.forte.simbot.common.annodatapropgen.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GenDataClass(
    val targetName: String = "",
    val propertiesMutable: Boolean = true
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Repeatable
annotation class GenDataClassFrom(
    val from: KClass<out Annotation> = GenDataClassFrom::class,
    val fromClass: String = "",
    /**
     * Target package name.
     */
    val to: String = SAME_AS_FROM,
    val targetName: String = "",
    val propertiesMutable: Boolean = true
) {
    companion object {
        const val SAME_AS_FROM = "$\$SAME_AS_FROM$$"
        const val SAME_AS_CURRENT = "$\$SAME_AS_CURRENT$$"
    }
}

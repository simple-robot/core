package love.forte.simbot.utils

/**
 * Copy and to immutable collections.
 */
public actual fun <T> Collection<T>.toImmutable(): Collection<T> {
    return if (this is Set<T>) {
        when {
            isEmpty() -> emptySet()
            size == 1 -> setOf(first())
            else -> toSet()
        }
    } else {
        when {
            isEmpty() -> emptyList()
            size == 1 -> listOf(first())
            else -> toList()
        }
    }
}

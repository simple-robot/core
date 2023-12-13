package love.forte.simbot.utils

import java.util.*

/**
 * Copy and to immutable collections.
 */
public actual fun <T> Collection<T>.toImmutable(): Collection<T> {
    return if (this is Set<T>) {
        when {
            isEmpty() -> emptySet()
            size == 1 -> setOf(first())
            else -> Collections.unmodifiableSet(toSet())
        }
    } else {
        when {
            isEmpty() -> emptyList()
            size == 1 -> listOf(first())
            else -> Collections.unmodifiableList(toList())
        }
    }
}

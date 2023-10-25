package love.forte.simbot.utils


/**
 * Copy and to immutable collections.
 */
public expect fun <T> Collection<T>.toImmutable(): Collection<T>

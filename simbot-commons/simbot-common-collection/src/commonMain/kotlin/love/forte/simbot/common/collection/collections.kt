package love.forte.simbot.common.collection


/**
 * Copy and to immutable collections.
 */
public expect fun <T> Collection<T>.toImmutable(): Collection<T>

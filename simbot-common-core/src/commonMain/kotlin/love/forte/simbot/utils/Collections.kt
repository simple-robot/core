package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.produceIn


/**
 * Copy and to immutable collections.
 */
public expect fun <T> Collection<T>.toImmutable(): Collection<T>

/**
 * Converts a [Flow] into an [Iterator] by providing custom implementations for the `hasNext` and `next` functions.
 *
 * @param producerScope The [CoroutineScope] that the produced [Flow] will be associated with.
 * @param hasNext A lambda expression that returns `true` if there are more elements in the [Flow], or `false` otherwise.
 * @param next A lambda expression that returns the next element in the [Flow].
 * @return An [Iterator] that can be used to iterate over the elements in the [Flow].
 */
public inline fun <T> Flow<T>.asIterator(
    producerScope: CoroutineScope,
    crossinline hasNext: ChannelIterator<T>.() -> Boolean,
    crossinline next: ChannelIterator<T>.() -> T
): Iterator<T> {
    val iterator = produceIn(producerScope).iterator()
    return iterator {
        while (hasNext(iterator)) {
            val nextValue = next(iterator)
            yield(nextValue)
        }
    }
}

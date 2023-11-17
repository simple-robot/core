@file:JvmName("CollectablesJVM")

package love.forte.simbot.collection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.function.Action
import java.util.stream.Stream
import kotlin.streams.asStream


/**
 * 将一个 [Collectable] 转化为可以提供同步迭代能力的迭代器 [SynchronouslyIterateCollectable]。
 *
 * 如果本身就属于 [SynchronouslyIterateCollectable] 类型则会得到自身，否则会尝试通过阻塞函数转化。
 * 这种情况下得到的 [SynchronouslyIterateCollectable] 可能会有一定的性能损耗，
 * 因为这其中隐含了将挂起函数转化为阻塞函数的行为。
 *
 */
public fun <T> Collectable<T>.asSynchronouslyIterateCollectable(): SynchronouslyIterateCollectable<T> {
    if (this is SynchronouslyIterateCollectable<T>) {
        return this
    }

    if (isEmptyCollectable()) {
        return EmptySynchronouslyIterateCollectable
    }

    if (isFlowCollectable()) {
        asFlow() // TODO
    }

    TODO()

}

private data object EmptySynchronouslyIterateCollectable : SynchronouslyIterateCollectable<Nothing> {
    override fun forEach(action: Action<Nothing>) {
    }

    override fun asFlow(): Flow<Nothing> = emptyFlow()
    override fun iterator(): Iterator<Nothing> = emptyList<Nothing>().iterator()
}

/**
 * Converts an [IterableCollectable] to a [Stream].
 *
 * @see Sequence.asStream
 * @return the Stream representation of the [IterableCollectable].
 */
public fun <T> IterableCollectable<T>.asStream(): Stream<T> = asSequence().asStream()

/**
 * Converts an [SequenceCollectable] to a [Stream].
 *
 * @see Sequence.asStream
 * @return the Stream representation of the [SequenceCollectable].
 */
public fun <T> SequenceCollectable<T>.asStream(): Stream<T> = asSequence().asStream()

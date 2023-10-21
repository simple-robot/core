
@file:JvmName("CollectablesJVM")
package love.forte.simbot.collection


/**
 * 将一个 [Collectable] 转化为可以提供非挂起迭代器的 [IterableCollectable]。
 *
 * 如果本身就属于 [IterableCollectable] 类型则会得到自身，否则会尝试通过阻塞函数转化。
 * 这种情况下得到的 [IterableCollectable] 可能会有一定的性能损耗，
 * 因为这其中隐含了将挂起函数转化为阻塞函数的行为。
 *
 */
public fun <T> Collectable<T>.asIterableCollectable(): IterableCollectable<T> {

    TODO()

}


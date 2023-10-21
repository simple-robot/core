/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.collection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import love.forte.simbot.function.Action
import kotlin.jvm.JvmSynthetic

/**
 * 一个可收集序列器。
 *
 * [Collectable] 应当支持将自身（或其中实际的元素收集方式）
 * 转化为可挂起的流（[Flow]）或同步序列（[Sequence], see [IterableCollectable]）。
 *
 * [Collectable] 不保证可以多次使用，也不建议多次调用 [Collectable] 中的API。
 * 无论是对于收集还是转化，同一个实例都应仅调用其中某API的一个。
 * 如果需要多次使用，请考虑将其转化为可安全重复使用的类型（例如 [List]）后再使用。
 *
 * 当 [Collectable] 中实际没有需要被挂起的类型时，考虑对外提供普通可迭代的收集器类型 [IterableCollectable]。
 *
 * @see IterableCollectable
 *
 * @author ForteScarlet
 */
public interface Collectable<out T> {

    /**
     * 挂起并收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     */
    @JvmSynthetic
    public suspend fun collect(collector: Action<T>)

    /**
     * 异步收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     */
    public fun launchAndCollect(scope: CoroutineScope, collector: Action<T>): Job =
        scope.launch { collect(collector) }

    /**
     * 将自身中的元素（或收集器）转化为 [Flow]。
     */
    public fun asFlow(): Flow<T>

}

/**
 * 一个可迭代的 [Collectable] 实现。
 *
 * [IterableCollectable] 中没有实际需要挂起的流类型（例如 [Flow]），
 * 因此可以将其转化为普通序列（例如 [Sequence]）或进行一些非挂起的操作（例如 [forEach]）。
 *
 * @see Collectable
 */
public interface IterableCollectable<out T> : Collectable<T> {

    /**
     * 普通地迭代当前收集器中的元素。
     */
    public fun forEach(action: Action<T>)

    /**
     * 收集当前元素。等同于 [forEach]，无挂起行为。
     */
    @JvmSynthetic
    override suspend fun collect(collector: Action<T>): Unit = forEach(collector)

    /**
     * 异步收集其中的元素。
     *
     * [collector] 函数体内不可挂起，如果希望行为函数内部可挂起请使用 [asFlow]
     * 转化为 [Flow] 后进行操作。
     *
     * 相当于异步地执行 [forEach]。
     *
     */
    override fun launchAndCollect(scope: CoroutineScope, collector: Action<T>): Job =
        scope.launch { forEach(collector) }

    /**
     * 将自身中的元素（或收集器）转化为 [Sequence]。
     */
    public fun asSequence(): Sequence<T>

    /**
     * 转化为 [Flow]。
     *
     * 默认实现会通过 [asSequence] 转化为一个冷流。
     * 具体实现中可能会重写此函数来提供更好的方案。
     */
    override fun asFlow(): Flow<T> = asSequence().asFlow()

}

// SequencedCollectable


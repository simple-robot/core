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
import kotlinx.coroutines.launch
import love.forte.simbot.function.Action
import kotlin.jvm.JvmSynthetic

// TODO Collectable..?

/**
 * 一个可收集序列器。
 *
 * [Collectable] 应当支持将自身（或其中实际的元素收集方式）
 * 转化为可挂起的流（[Flow]）或同步序列（[Sequence]）。
 *
 * [Collectable] 本身可能含有一个热流或冷流，因此 [Collectable] 不保证可以多次使用，
 * 也不建议多次调用 [Collectable] 的转化函数。[Collectable] 应当仅至多调用一次转化函数。
 *
 * @author ForteScarlet
 */
public interface Collectable<out T> {

    // TODO ?

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
    @JvmSynthetic
    public fun launchAndCollect(scope: CoroutineScope, collector: Action<T>): Job = scope.launch { collect(collector) }

    /**
     * 将自身中的元素（或收集器）转化为 [Flow]。
     */
    public fun asFlow(): Flow<T>

}


/**
 * 一个基于普通迭代器 [Collectable] 实现。
 *
 * [IterableCollectable] 中没有实际需要挂起的流类型（例如 [Flow]），
 * 因此可以将其转化为普通序列（例如 [Sequence]）。
 *
 */
public interface IterableCollectable<out T> : Collectable<T> {

    /**
     *
     */
    public fun forEach(action: Action<T>)

    @JvmSynthetic
    override suspend fun collect(collector: Action<T>): Unit = forEach(collector)


    override fun launchAndCollect(scope: CoroutineScope, collector: Action<T>): Job = scope.launch {
        forEach(collector)
    }


    /**
     * 将自身中的元素（或收集器）转化为 [Sequence]。
     */
    public fun asSequence(): Sequence<T>

}



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

@file:JvmName("Collectables")

package love.forte.simbot.collection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.function.Action
import kotlin.jvm.JvmName

/**
 * 得到一个没有元素的 [Collectable]。
 */
public fun <T> emptyCollectable(): Collectable<T> = EmptyCollectable

private data object EmptyCollectable : IterableCollectable<Nothing> {
    override suspend fun collect(collector: Action<Nothing>) {
        // nothing.
    }

    override fun forEach(action: Action<Nothing>) {
    }

    override fun launchAndCollect(scope: CoroutineScope, collector: Action<Nothing>): Job {
        return Job().apply { complete() }
    }

    override fun asFlow(): Flow<Nothing> = emptyFlow()

    override fun asSequence(): Sequence<Nothing> = emptySequence()
}

/**
 * 将一个 [Flow] 转化为 [Collectable]。
 *
 */
public fun <T> Flow<T>.asCollectable(): Collectable<T> = FlowCollectable(this)

// TODO Expect

private class FlowCollectable<T>(private val flow: Flow<T>) : Collectable<T> {
    override fun asFlow(): Flow<T> = flow
    override suspend fun collect(collector: Action<T>) {
        flow.collect { collector(it) }
    }
}

// TODO

public fun <T> Iterable<T>.asCollectable(): Collectable<T> = IterableCollectableImpl(this)


private class IterableCollectableImpl<T>(private val collection: Iterable<T>) : IterableCollectable<T> {
    override fun asFlow(): Flow<T> = collection.asFlow()
    override fun asSequence(): Sequence<T> = collection.asSequence()

    override fun forEach(action: Action<T>): Unit = collection.forEach(action::invoke)
}

// TODO

public fun <T> Sequence<T>.asCollectable(): Collectable<T> = SequenceCollectable(this)


private class SequenceCollectable<T>(private val collection: Sequence<T>) : IterableCollectable<T> {
    override fun asFlow(): Flow<T> = collection.asFlow()
    override fun asSequence(): Sequence<T> = collection

    override fun forEach(action: Action<T>): Unit = collection.forEach(action::invoke)
}

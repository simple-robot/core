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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import love.forte.simbot.annotations.InternalAPI
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.Promise


/**
 * 执行一个异步函数，得到 [Promise].
 */
@InternalAPI
public inline fun <T> runInPromise(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T,
): Promise<T> =
    scope.promise(context) { block() }

/**
 * 使用 [GlobalScope] 执行一个异步函数，得到 [Promise].
 */
@DelicateCoroutinesApi
@InternalAPI
public inline fun <T> runInPromise(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T,
): Promise<T> = runInPromise(scope = GlobalScope, context, block)

/**
 * 使用 [GlobalScope] 执行一个异步函数，得到 [Promise].
 */
@OptIn(DelicateCoroutinesApi::class)
@Suppress("FunctionName")
@InternalAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInPromise`(
    scope: CoroutineScope? = null,
    block: suspend () -> T
): Promise<T> = runInPromise(scope = scope ?: GlobalScope, EmptyCoroutineContext) { block() }


/**
 * @see SuspendReserve
 */
@InternalAPI
@OptIn(DelicateCoroutinesApi::class)
public fun <T> asReserve(
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null,
    block: suspend () -> T
): SuspendReserve<T> =
    suspendReserve(scope = scope ?: GlobalScope, context = context ?: EmptyCoroutineContext, block = block)

/**
 * @see asReserve
 */
@Suppress("FunctionName")
@InternalAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$asReserve`(scope: CoroutineScope? = null, block: suspend () -> T): SuspendReserve<T> =
    asReserve(scope = scope, context = EmptyCoroutineContext, block = block)

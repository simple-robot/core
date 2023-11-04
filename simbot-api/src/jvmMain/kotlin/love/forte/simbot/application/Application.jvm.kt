@file:JvmName("Applications")
@file:JvmMultifileClass

package love.forte.simbot.application

import kotlinx.coroutines.Job
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

/**
 * 将 [Application] 转作 [CompletableFuture]。
 *
 * @see Job.asCompletableFuture
 */
public fun Application.asCompletableFuture(): CompletableFuture<Unit> =
    coroutineContext[Job]?.asCompletableFuture() ?: CompletableFuture.completedFuture(Unit)

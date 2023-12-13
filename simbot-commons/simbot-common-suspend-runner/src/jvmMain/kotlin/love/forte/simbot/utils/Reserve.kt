package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.annotations.InternalAPI
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

/**
 * A class representing a coroutine-based reserve.
 *
 * @param T the type of the result produced by the reserve
 * @property scope the [CoroutineScope] to use for launching coroutines
 * @property context the [CoroutineContext] to use for running the coroutine
 * @property block the suspend function block that represents the reserve logic
 */
public class Reserve<out T>(private val scope: CoroutineScope, private val context: CoroutineContext, private val block: suspend () -> T) {

    /**
     * Executes the given block of code in a no-scope blocking manner.
     *
     * @return the result of executing the block
     */
    public fun block(): T = runInNoScopeBlocking(context) { block.invoke() }

    /**
     * Executes the given block asynchronously and returns a CompletableFuture that represents the result.
     *
     * @return A CompletableFuture that represents the result of the asynchronous operation.
     */
    @OptIn(InternalAPI::class)
    public fun async(): CompletableFuture<out T> = runInAsync(scope, context) { block.invoke() }
}


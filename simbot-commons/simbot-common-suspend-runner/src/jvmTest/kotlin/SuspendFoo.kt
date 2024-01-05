import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import love.forte.simbot.annotations.InternalAPI
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * @author ForteScarlet
 */
class SuspendFoo : CoroutineScope {
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext

    @JvmSynthetic
    suspend fun run(name: String): String {
        delay(100)
        return name
    }

    @OptIn(InternalAPI::class)
    fun runReserve(name: String) = suspendReserve(this, EmptyCoroutineContext) { run(name) }

}

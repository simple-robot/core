import kotlinx.coroutines.delay
import love.forte.simbot.suspendrunner.InvokeStrategy
import love.forte.simbot.utils.runInAsync
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

object JInvokeStrategies {

    @JvmStatic
    fun <T> async(): InvokeStrategy<T, CompletableFuture<T>> =
        AsyncInvokeStrategy as InvokeStrategy<T, CompletableFuture<T>>

    @JvmStatic
    fun <T> block(): InvokeStrategy<T, T> = BlockingInvokeStrategy as InvokeStrategy<T, T>

}


private object AsyncInvokeStrategy : InvokeStrategy<Any?, CompletableFuture<*>> {
    override fun <T : Any?> invoke(block: suspend () -> T): CompletableFuture<T> {
        return runInAsync { block() }
    }
}

private object BlockingInvokeStrategy : InvokeStrategy<Any?, Any?> {
    override fun <T> invoke(block: suspend () -> T): T = runInNoScopeBlocking { block() }

}


class FooRunner {

    @JvmSynthetic
    suspend fun run(): String {
        delay(1)
        return "Hello"
    }

    fun <R> run(strategy: InvokeStrategy<String, R>): R = strategy.invoke { run() }
}

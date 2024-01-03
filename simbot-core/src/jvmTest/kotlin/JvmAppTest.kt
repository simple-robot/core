import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import love.forte.simbot.application.launchApplication
import love.forte.simbot.core.application.Simple
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.addEachScopeInterceptor
import love.forte.simbot.id.ID
import love.forte.simbot.id.UUID
import love.forte.simbot.utils.PriorityConstant
import java.util.concurrent.Executors

/**
 * 测试事件调度与结果流收集
 */
suspend fun main() {
    val dispatcherPool = Executors.newFixedThreadPool(4) {
        Thread(it, "DIS-P").apply {

            isDaemon = true
        }
    }.asCoroutineDispatcher()

    val application = launchApplication(Simple) {
        stageEvents {

        }

        eventDispatcher {
            // coroutineContext += dispatcherPool
            addEachScopeInterceptor {
                println("==> 拦截器1")
                val invoke = it.invoke()
                println("<== 拦截器1")
                invoke
            }
            addEachScopeInterceptor {
                println("==> 拦截器B")
                val invoke = it.invoke()
                println("<== 拦截器B")
                invoke
            }
        }

    }

    val listener = EventListener { event ->
        println("[${Thread.currentThread()}] On Event A: $event")
        EventResult.empty()
    }

    application.eventDispatcher.register(listener)
    application.eventDispatcher.register(listener) {
        addInterceptor({
            println("==> 独特拦截器C")
            val invoke = it.invoke()
            println("<== 独特拦截器C")
            invoke
        }) {
            priority = PriorityConstant.AFTER_1
        }
    }

    application.eventDispatcher.push(TestEvent())
        // .flowOn(Dispatchers.IO)
        .flowOn(dispatcherPool)
        .collect {
            println("[${Thread.currentThread()}] On Result: $it")
        }

    application.cancel()
}


private class TestEvent : Event {
    override val id: ID = UUID.random()
}

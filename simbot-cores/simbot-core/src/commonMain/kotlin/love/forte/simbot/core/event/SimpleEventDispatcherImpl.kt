package love.forte.simbot.core.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import love.forte.simbot.event.*
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.utils.MutableAttributeMap
import love.forte.simbot.utils.PriorityConstant
import love.forte.simbot.utils.concurrentMutableMap
import love.forte.simbot.utils.mutableAttributeMapOf
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


internal class SimpleEventDispatcherConfigurationImpl : AbstractEventDispatcherConfiguration(),
    SimpleEventDispatcherConfiguration {
    public override val interceptors: MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>
        get() = super.interceptors

    public override val dispatchInterceptors: MutableList<Pair<EventDispatchInterceptor, ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?>>
        get() = super.dispatchInterceptors
}

private class SimpleEventInterceptorRegistrationPropertiesImpl : SimpleEventInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

private class SimpleEventDispatchInterceptorRegistrationPropertiesImpl :
    SimpleEventDispatchInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

private class SimpleEventListenerRegistrationPropertiesImpl(private val interceptorBuilder: SimpleEventInterceptorsBuilder) :
    SimpleEventListenerRegistrationProperties {

    override var priority: Int = PriorityConstant.NORMAL

    override fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    ) {
        interceptorBuilder.addInterceptor(interceptor, propertiesConsumer)
    }
}

private class SimpleEventInterceptorsBuilder {
    private val interceptors: MutableList<SimpleEventInterceptorInvoker> = mutableListOf()
    private val dispatchInterceptors: MutableList<SimpleEventDispatchInterceptorInvoker> = mutableListOf()

    fun addInterceptor(
        interceptor: EventInterceptor,
        configurer: ConfigurerFunction<EventInterceptorRegistrationProperties>? = null
    ) {
        val prop = SimpleEventInterceptorRegistrationPropertiesImpl().also { configurer?.invokeWith(it) }
        val invoker = SimpleEventInterceptorInvoker(interceptor, prop.priority)
        interceptors.add(invoker)
    }

    fun addDispatchInterceptor(
        interceptor: EventDispatchInterceptor,
        configurer: ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>? = null
    ) {
        val prop = SimpleEventDispatchInterceptorRegistrationPropertiesImpl().also { configurer?.invokeWith(it) }
        val invoker = SimpleEventDispatchInterceptorInvoker(interceptor, prop.priority)
        dispatchInterceptors.add(invoker)
    }

    fun build(): SimpleEventInterceptors = SimpleEventInterceptors(interceptors.sorted(), dispatchInterceptors.sorted())
}

private class SimpleEventInterceptors(
    val interceptors: List<SimpleEventInterceptorInvoker>,
    val dispatchInterceptors: List<SimpleEventDispatchInterceptorInvoker>,
)


private class SimpleEventInterceptorInvoker(
    private val interceptor: EventInterceptor,
    private val priority: Int
) : Comparable<SimpleEventInterceptorInvoker>, EventInterceptor by interceptor {
    override fun compareTo(other: SimpleEventInterceptorInvoker): Int = priority.compareTo(other.priority)
}

private class SimpleEventDispatchInterceptorInvoker(
    private val interceptor: EventDispatchInterceptor,
    private val priority: Int
) : Comparable<SimpleEventDispatchInterceptorInvoker>, EventDispatchInterceptor by interceptor {
    override fun compareTo(other: SimpleEventDispatchInterceptorInvoker): Int = priority.compareTo(other.priority)
}

private class SimpleEventInterceptorsInvoker(private val interceptors: Iterable<SimpleEventInterceptorInvoker>) {
    private class ContextImpl(
        override val eventListenerContext: EventListenerContext,
        val iterator: Iterator<EventInterceptor>,
        private val actualTarget: suspend (EventListenerContext) -> EventResult
    ) : EventInterceptor.Context {
        override suspend fun invoke(): EventResult {
            return if (iterator.hasNext()) {
                iterator.next().intercept(this)
            } else {
                actualTarget(eventListenerContext)
            }
        }

        override suspend fun invoke(eventListenerContext: EventListenerContext): EventResult {
            return if (iterator.hasNext()) {
                iterator.next().intercept(copy(eventListenerContext))
            } else {
                actualTarget(eventListenerContext)
            }
        }

        private fun copy(eventListenerContext: EventListenerContext): ContextImpl =
            ContextImpl(eventListenerContext, iterator, actualTarget)
    }

    suspend fun invoke(eventContext: EventListenerContext, actualTarget: suspend (EventListenerContext) -> EventResult): EventResult {
        val context = ContextImpl(eventContext, interceptors.iterator(), actualTarget)
        return context.invoke()
    }
}

private class SimpleEventDispatchInterceptorsInvoker(private val interceptors: Iterable<SimpleEventDispatchInterceptorInvoker>) {
    private class ContextImpl(
        override val eventContext: EventContext,
        val iterator: Iterator<EventDispatchInterceptor>,
        private val actualTarget: (EventContext) -> Flow<EventResult>
    ) : EventDispatchInterceptor.Context {
        override fun invoke(): Flow<EventResult> {
            return if (iterator.hasNext()) {
                iterator.next().intercept(this)
            } else {
                actualTarget(eventContext)
            }
        }

        override fun invoke(eventContext: EventContext): Flow<EventResult> {
            return if (iterator.hasNext()) {
                iterator.next().intercept(copy(eventContext))
            } else {
                actualTarget(eventContext)
            }
        }

        private fun copy(eventContext: EventContext): ContextImpl =
            ContextImpl(eventContext, iterator, actualTarget)
    }

    fun invoke(
        eventContext: EventContext,
        actualTarget: (EventContext) -> Flow<EventResult>
    ): Flow<EventResult> {
        val context = ContextImpl(eventContext, interceptors.iterator(), actualTarget)
        return context.invoke()
    }
}

/**
 *
 * @author ForteScarlet
 */
internal class SimpleEventDispatcherImpl(
    private val configuration: SimpleEventDispatcherConfigurationImpl,
) : SimpleEventDispatcher {
    //region Interceptors
    private val interceptors = SimpleEventInterceptorsBuilder().apply {
        configuration.interceptors.forEach { (interceptor, prop) ->
            addInterceptor(interceptor, prop)
        }
        configuration.dispatchInterceptors.forEach { (interceptor, prop) ->
            addDispatchInterceptor(interceptor, prop)
        }
    }.build()

    private val dispatchInterceptorsInvoker =
        interceptors.dispatchInterceptors.takeIf { it.isNotEmpty() }?.let { SimpleEventDispatchInterceptorsInvoker(it) }
    //SimpleEventDispatchInterceptorsInvoker(interceptors.dispatchInterceptors)

    private val dispatcherContext = configuration.coroutineContext.minusKey(Job)

    //endregion

    private val listenersQueue = createPriorityConcurrentQueue<SimpleEventListenerInvoker>()

    override val listeners: Sequence<EventListener>
        get() = listenersQueue.asSequence().map { it.listener }

    override fun register(
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?,
        listener: EventListener
    ): EventListenerRegistrationHandle {
        val interceptorBuilder = SimpleEventInterceptorsBuilder()
        val prop = SimpleEventListenerRegistrationPropertiesImpl(interceptorBuilder).also { prop ->
            propertiesConsumer?.invokeWith(prop)
        }

        // interceptors
        val listenerScopeInterceptors = interceptorBuilder.build().interceptors
        val eachInterceptors = interceptors.interceptors
        val listenerInterceptors = (listenerScopeInterceptors + eachInterceptors).sorted()

        val priority = prop.priority
        val listenerInvoker = SimpleEventListenerInvoker(listenerInterceptors, listener)

        listenersQueue.add(priority, listenerInvoker)

        return createQueueRegistrationHandle(priority, listenersQueue, listenerInvoker)
    }

    override fun dispose(listener: EventListener) {
        listenersQueue.removeIf { it.listener == listener }
    }


    override fun push(event: Event): Flow<EventResult> {
        return pushWithInterceptor(event)
    }

    private fun pushWithInterceptor(event: Event): Flow<EventResult> {
        val context = EventContextImpl(event)

        return runCatching {
            dispatchInterceptorsInvoker?.invoke(context) { eventFlow(context) } ?: eventFlow(context)
        }.getOrElse { e ->
            return flow {
                // emit exception
                throw e
            }
        }

    }

    private fun eventFlow(context: EventContext): Flow<EventResult> {
        return if (dispatcherContext == EmptyCoroutineContext) {
            flow {
                dispatchInFlowWithoutCoroutineContext(context, this)
            }
        } else {
            flow {
                dispatchInFlow(context, dispatcherContext, this)
            }
        }
    }

    private data class EventContextImpl(
        override val event: Event,
        override val attributes: MutableAttributeMap = mutableAttributeMapOf(concurrentMutableMap())
    ) : EventContext


    private suspend fun SimpleEventListenerInvoker.invokeAndCollectedOrErrorResult(context: EventListenerContext): EventResult =
        orErrorResult { invoke(context).collected() }

    private suspend fun dispatchInFlow(
        context: EventContext,
        dispatcherContext: CoroutineContext,
        collector: FlowCollector<EventResult>
    ) {
        val listenerIterator = listenersQueue.iterator()

        for (listenerInvoker in listenerIterator) {
            val lContext = EventListenerContextImpl(context)
            val result = withContext(dispatcherContext) {
                listenerInvoker.invokeAndCollectedOrErrorResult(lContext)
            }

            collector.emit(result)

            if (result.isTruncated) {
                break
            }
        }
    }

    private suspend fun dispatchInFlowWithoutCoroutineContext(
        context: EventContext,
        collector: FlowCollector<EventResult>
    ) {
        val listenerIterator = listenersQueue.iterator()
        for (listenerInvoker in listenerIterator) {
            val lContext = EventListenerContextImpl(context)
            val result = listenerInvoker.invokeAndCollectedOrErrorResult(lContext)

            collector.emit(result)

            if (result.isTruncated) {
                break
            }
        }
    }

    private inline fun orErrorResult(block: () -> EventResult): EventResult =
        runCatching { block() }.getOrElse { e -> EventResult.error(e) }

    override fun toString(): String {
        return "SimpleEventDispatcher"
    }
}

private data class EventListenerContextImpl(override val context: EventContext) : EventListenerContext {
    @Volatile
    override var plainText: String? = (context.event as? MessageEvent)?.messageContent?.plainText
}


private class SimpleEventListenerInvoker(
    /**
     * 合并了全局配置的拦截器和单独添加的拦截器
     */
    interceptors: List<SimpleEventInterceptorInvoker>,
    val listener: EventListener
) {
    private val interceptorsInvoker = interceptors.takeIf { it.isNotEmpty() }?.let { interceptorList ->
        SimpleEventInterceptorsInvoker(interceptorList)
    }

    suspend fun invoke(context: EventListenerContext): EventResult {
        return interceptorsInvoker?.invoke(context, listener::handle) ?: listener.handle(context)
    }

}


internal expect fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle

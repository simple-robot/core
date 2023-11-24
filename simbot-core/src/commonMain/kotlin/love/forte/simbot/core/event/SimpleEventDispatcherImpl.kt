package love.forte.simbot.core.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import love.forte.simbot.event.*
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.utils.PriorityConstant


internal class SimpleEventDispatcherConfigurationImpl : AbstractEventDispatcherConfiguration(),
    SimpleEventDispatcherConfiguration {
    public override val interceptors: MutableMap<EventInterceptor.Scope, MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>>
        get() = super.interceptors
}

private class SimpleEventInterceptorRegistrationPropertiesImpl : SimpleEventInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

private class SimpleEventListenerRegistrationPropertiesImpl(private val interceptorBuilder: SimpleEventInterceptorsBuilder) :
    SimpleEventListenerRegistrationProperties {
    companion object {
        val INTERCEPTOR_SCOPE = EventInterceptor.Scope.GLOBAL
    }

    override var priority: Int = PriorityConstant.NORMAL

    override fun addInterceptor(
        interceptor: EventInterceptor,
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?
    ) {
        interceptorBuilder.addInterceptor(INTERCEPTOR_SCOPE, interceptor, propertiesConsumer)
    }
}

private class SimpleEventInterceptorsBuilder {
    private val interceptors: MutableMap<EventInterceptor.Scope, MutableList<SimpleEventInterceptorInvoker>> =
        mutableMapOf()

    fun addInterceptor(
        scope: EventInterceptor.Scope,
        interceptor: EventInterceptor,
        configurer: ConfigurerFunction<EventInterceptorRegistrationProperties>? = null
    ) {
        val prop = SimpleEventInterceptorRegistrationPropertiesImpl().also { configurer?.invokeWith(it) }
        val invoker = SimpleEventInterceptorInvoker(interceptor, prop.priority)
        val list = interceptors.getOrPut(scope) { mutableListOf() }
        list.add(invoker)
    }

    fun build(): SimpleEventInterceptors = SimpleEventInterceptors(interceptors.toMap().mapValues { (_, v) ->
        v.sorted().toList()
    })
}

private class SimpleEventInterceptors(
    private val interceptors: Map<EventInterceptor.Scope, Iterable<SimpleEventInterceptorInvoker>>
) {
    operator fun get(scope: EventInterceptor.Scope): Iterable<SimpleEventInterceptorInvoker> =
        interceptors[scope] ?: emptyList()
}


private class SimpleEventInterceptorInvoker(
    private val interceptor: EventInterceptor,
    private val priority: Int
) : Comparable<SimpleEventInterceptorInvoker>, EventInterceptor by interceptor {
    override fun compareTo(other: SimpleEventInterceptorInvoker): Int = priority.compareTo(other.priority)
}

private class SimpleEventInterceptorsInvoker(private val interceptors: Iterable<SimpleEventInterceptorInvoker>) {
    private class ContextImpl(
        override val eventContext: EventContext,
        val iterator: Iterator<EventInterceptor>,
        private val actualTarget: suspend (EventContext) -> EventResult
    ) : EventInterceptor.Context {
        override suspend fun invoke(): EventResult {
            return if (iterator.hasNext()) {
                iterator.next().intercept(this)
            } else {
                actualTarget(eventContext)
            }
        }
    }

    suspend fun invoke(eventContext: EventContext, actualTarget: suspend (EventContext) -> EventResult): EventResult {
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
        configuration.interceptors.forEach { (scope, interceptors) ->
            interceptors.forEach { (interceptor, configurer) ->
                addInterceptor(scope, interceptor, configurer)
            }
        }
    }.build()

    private val dispatcherContext = configuration.coroutineContext.minusKey(Job)

    //endregion

    private val listeners = createPriorityConcurrentQueue<SimpleEventListenerInvoker>()

    override fun register(
        listener: EventListener,
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?
    ): EventListenerRegistrationHandle {
        val interceptorBuilder = SimpleEventInterceptorsBuilder()
        val prop = SimpleEventListenerRegistrationPropertiesImpl(interceptorBuilder).also { prop ->
            propertiesConsumer?.invokeWith(prop)
        }

        // interceptors
        val listenerScopeInterceptors =
            interceptorBuilder.build()[SimpleEventListenerRegistrationPropertiesImpl.INTERCEPTOR_SCOPE]
        val eachInterceptors = interceptors[EventInterceptor.Scope.EACH]
        val listenerInterceptors = (listenerScopeInterceptors + eachInterceptors).sorted()

        val priority = prop.priority
        val listenerInvoker = SimpleEventListenerInvoker(listenerInterceptors, listener, priority)

        listeners.add(priority, listenerInvoker)

        return createQueueRegistrationHandle(priority, listeners, listenerInvoker)
    }


    override fun push(event: Event): Flow<EventResult> {
        return flow {
            push0(EventContextImpl(event), this)
        }
    }


    private data class EventContextImpl(override val event: Event) : EventContext


    private suspend fun push0(context: EventContext, collector: FlowCollector<EventResult>) {
        // TODO Global interceptor

        val listenerIterator = listeners.iterator()
        for (listenerInvoker in listenerIterator) {
            val result = withContext(dispatcherContext) {
                listenerInvoker.invoke(context)
            }
            collector.emit(result)
        }

    }

}


private class SimpleEventListenerInvoker(
    /**
     * 合并了全局的Each拦截器和单独添加的拦截器
     */
    interceptors: List<SimpleEventInterceptorInvoker>,
    private val listener: EventListener,
    private val priority: Int
) {
    private val interceptorsInvoker = interceptors.takeIf { it.isNotEmpty() }?.let { interceptorList ->
        SimpleEventInterceptorsInvoker(interceptorList)
    }

    suspend fun invoke(context: EventContext): EventResult {
        return interceptorsInvoker?.invoke(context, listener::handle) ?: listener.handle(context)
    }

}


internal expect fun <T : Any> createQueueRegistrationHandle(
    priority: Int,
    queue: PriorityConcurrentQueue<T>,
    target: T
): EventListenerRegistrationHandle

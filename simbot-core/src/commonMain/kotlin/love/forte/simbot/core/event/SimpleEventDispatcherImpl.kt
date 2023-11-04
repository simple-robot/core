package love.forte.simbot.core.event

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.event.*
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.utils.PriorityConstant


internal data class SimpleEventInterceptorRegistrationPropertiesImpl(override var priority: Int) :
    SimpleEventInterceptorRegistrationProperties

internal class SimpleEventDispatcherConfigurationImpl : AbstractEventDispatcherConfiguration(),
    SimpleEventDispatcherConfiguration {
    public override val interceptors: MutableMap<EventInterceptor.Scope, MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>>
        get() = super.interceptors
}

// TODO except create Enum Map
// TODO except create Ordered Map

private class EventInterceptorRegistrationPropertiesImpl : SimpleEventInterceptorRegistrationProperties {
    override var priority: Int = PriorityConstant.NORMAL
}

/**
 *
 * @author ForteScarlet
 */
internal class SimpleEventDispatcherImpl(
    private val configuration: SimpleEventDispatcherConfigurationImpl,

    ) : SimpleEventDispatcher {
    private val interceptors =
        configuration.interceptors.mapValues { (_, v) ->
            v.map { (interceptor, propConfigurer) ->
                val prop = EventInterceptorRegistrationPropertiesImpl().also { propConfigurer?.invokeWith(it) }
                EventInterceptorInvoker(interceptor, prop.priority)
            }.sorted()
        }

    private class EventInterceptorInvoker(
        private val interceptor: EventInterceptor,
        private val priority: Int
    ) : Comparable<EventInterceptorInvoker>, EventInterceptor by interceptor {
        override fun compareTo(other: EventInterceptorInvoker): Int = priority.compareTo(other.priority)
    }

    // Interceptor
    // TODO InterceptorsInvoker, invoke multi interceptors.

    override fun push(event: Event): Flow<EventResult> {
        TODO("Not yet implemented")
    }

    override fun register(
        listener: EventListener,
        propertiesConsumer: ConfigurerFunction<EventListenerRegistrationProperties>?
    ): EventListenerRegistrationHandle {
        TODO("Not yet implemented")
    }
}

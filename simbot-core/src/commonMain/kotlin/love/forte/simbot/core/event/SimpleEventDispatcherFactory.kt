package love.forte.simbot.core.event

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.event.*
import love.forte.simbot.function.ConfigurerFunction


internal data class SimpleEventInterceptorRegistrationPropertiesImpl(override var priority: Int) :
    SimpleEventInterceptorRegistrationProperties

internal class SimpleEventDispatcherConfigurationImpl : AbstractEventDispatcherConfiguration(),
    SimpleEventDispatcherConfiguration {
    public override val interceptors: MutableMap<EventInterceptor.Scope, MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>>>
        get() = super.interceptors
}

// except create Ordered Map

/**
 *
 * @author ForteScarlet
 */
internal class SimpleEventDispatcherImpl(
    private val configuration: SimpleEventDispatcherConfigurationImpl,

) : SimpleEventDispatcher {
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

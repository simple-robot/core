package love.forte.simbot.spring.application.internal

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.event.*
import love.forte.simbot.spring.application.SpringEventDispatcherConfiguration
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class SpringEventDispatcherConfigurationImpl(
    internal val simple: SimpleEventDispatcherConfigurationImpl
) : AbstractEventDispatcherConfiguration(),
    SpringEventDispatcherConfiguration {
    override var coroutineContext: CoroutineContext by simple::coroutineContext
    public override val interceptors: MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>> by simple::interceptors
    public override val dispatchInterceptors: MutableList<Pair<EventDispatchInterceptor, ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?>> by simple::dispatchInterceptors
}

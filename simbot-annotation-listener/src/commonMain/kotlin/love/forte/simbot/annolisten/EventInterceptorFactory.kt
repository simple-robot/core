package love.forte.simbot.annolisten

import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.event.EventInterceptorRegistrationProperties
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.function.plus
import kotlin.jvm.JvmStatic


/**
 *
 * @author ForteScarlet
 */
public fun interface EventInterceptorFactory {

    public fun interceptor(properties: EventInterceptorRegistrationProperties): Result?


    public abstract class Result {
        public abstract val interceptor: EventInterceptor
        public abstract val configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>?

        public companion object {

            @JvmStatic
            public fun build(block: ConfigurerFunction<Builder>): Result =
                EventInterceptorFactoryResultBuilderImpl().also(block::invokeWith).build()



        }


        public interface Builder {
            public fun interceptor(interceptor: EventInterceptor): Builder
            public fun configuration(configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>): Builder
        }

    }



}

private class EventInterceptorFactoryResultBuilderImpl : EventInterceptorFactory.Result.Builder {
    private lateinit var interceptor: EventInterceptor
    private var configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>? = null

    override fun interceptor(interceptor: EventInterceptor): EventInterceptorFactory.Result.Builder = apply {
        this.interceptor = interceptor
    }

    override fun configuration(configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>): EventInterceptorFactory.Result.Builder = apply {
        if (this.configuration == null) {
            this.configuration = configuration
        } else {
            this.configuration = this.configuration?.let { thisConfiguration ->
                thisConfiguration + configuration
            }
        }
    }

    fun build(): EventInterceptorFactory.Result =
        ResultImpl(interceptor, configuration)

    private class ResultImpl(
        override val interceptor: EventInterceptor,
        override val configuration: ConfigurerFunction<EventInterceptorRegistrationProperties>?
    ) : EventInterceptorFactory.Result()
}

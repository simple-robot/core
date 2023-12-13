package love.forte.simbot.quantcat

import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerRegistrationProperties
import love.forte.simbot.quantcat.annotations.Filter
import kotlin.reflect.KFunction


/**
 *
 * @author ForteScarlet
 */
public interface FilterProcessor {
    // TODO FilterProcessor

    // return what?

    public fun process(context: Context): EventListener


    public interface Context {
        public val listenerProperties: EventListenerRegistrationProperties

        // TODO FilterProcessor.Context

        /**
         * [Filter] 注解标记的源函数。
         * 通常用于获取一些其他可能有用的或自定义的注解。
         *
         * _Note: Java 中可以使用 `ReflectJvmMapping.javaMethod` 转化为 `Method`。_
         *
         */
        public val function: KFunction<*>

        /**
         * 被标记的 [Filter] 注解对象
         */
        public val filter: Filter
    }



}


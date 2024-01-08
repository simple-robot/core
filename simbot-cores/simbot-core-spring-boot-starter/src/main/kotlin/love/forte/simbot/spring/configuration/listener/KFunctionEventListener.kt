package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.AttributeMap
import love.forte.simbot.common.attribute.AttributeMapContainer
import love.forte.simbot.common.attribute.attribute
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.quantcat.common.binder.FunctionalBindableEventListener
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


public abstract class KFunctionEventListener(instance: Any?, caller: KFunction<*>) : FunctionalBindableEventListener(instance, caller) {
    public abstract val attributes: AttributeMap

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {
        /**
         * [RawFunctionAttribute] 的属性名
         */
        public const val RAW_FUNCTION_ATTRIBUTE_NAME: String = "$\$RAW_FUNCTION$"

        /**
         * 记录 [KFunctionEventListener] 内含原始函数的属性
         */
        @JvmField
        public val RawFunctionAttribute: Attribute<KFunction<*>> = attribute(RAW_FUNCTION_ATTRIBUTE_NAME)

        /**
         * [RawBindersAttribute] 的属性名
         */
        public const val RAW_BINDERS_ATTRIBUTE_NAME: String = "$\$RAW_BINDERS$"

        /**
         * 记录 [KFunctionEventListener] 内含的最终绑定器集的属性。
         */
        @JvmField
        public val RawBindersAttribute: Attribute<Iterable<ParameterBinder>> = attribute(RAW_BINDERS_ATTRIBUTE_NAME)

        /**
         * [RawListenTargetAttribute] 的属性名
         */
        public const val RAW_LISTEN_TARGET_ATTRIBUTE_NAME: String = "$\$RAW_LISTEN_TARGET$"

        /**
         * 记录 [KFunctionEventListener] 内含的最终的监听目标事件的类型的属性。
         */
        @JvmField
        public val RawListenTargetAttribute: Attribute<KClass<out Event>> =
            attribute(RAW_LISTEN_TARGET_ATTRIBUTE_NAME)
    }
}


/**
 *
 * @author ForteScarlet
 */
internal class KFunctionEventListenerImpl(
    instance: Any?,
    caller: KFunction<*>,
    override val binders: Array<ParameterBinder>,
    override val attributes: AttributeMap,
    private val matcher: suspend (EventListenerContext) -> Boolean
) : KFunctionEventListener(instance, caller), AttributeMapContainer {
    override suspend fun match(context: EventListenerContext): Boolean = matcher(context)

    override val attributeMap: AttributeMap
        get() = attributes

    override fun toString(): String =
        "KFunctionEventListener(caller=$caller, attributes=$attributes)"
}

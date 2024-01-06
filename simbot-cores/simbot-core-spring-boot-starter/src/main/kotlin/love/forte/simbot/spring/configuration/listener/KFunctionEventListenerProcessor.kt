package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.event.Event
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.spring.MultipleIncompatibleTypesEventException
import org.springframework.context.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible


/**
 *
 * @author ForteScarlet
 */
internal class KFunctionEventListenerProcessor {
    private val instanceCache = ConcurrentHashMap<KClass<*>, Any>()

    fun process(function: KFunction<*>, applicationContext: ApplicationContext): EventListenerFactory {
        function.isAccessible = true
        val listenTarget = function.listenTarget()



        TODO()
    }


    companion object {
        private val logger = LoggerFactory.logger<KFunctionEventListenerProcessor>()
    }
}


/**
 * 解析此监听函数所期望监听的事件列表。
 */
@Suppress("UNCHECKED_CAST")
private fun KFunction<*>.listenTarget(): KClass<out Event> {
    val typeLink = mutableListOf<KClass<*>>()
    var minType: KClass<out Event>? = null

    parameters.asSequence()
        .filter { it.kind != KParameter.Kind.INSTANCE }
        .filter { (it.type.classifier as? KClass<*>)?.isSubclassOf(Event::class) == true }
        .forEach {
            val e = it.type.classifier as KClass<out Event>
            val m = minType
            when {
                m == null -> {
                    minType = e
                    typeLink.add(e)
                }

                e == m -> {
                    // do nothing.
                }

                e.isSubclassOf(m) -> {
                    minType = e
                    typeLink.add(e)
                }

                else -> {
                    throw MultipleIncompatibleTypesEventException(buildString {
                        append("Current Event types link of function [${this@listenTarget}] is: \n[")
                        typeLink.forEachIndexed { index, t ->
                            append("(")
                            append(t)
                            append(")")
                            if (index != typeLink.lastIndex) {
                                append(" -> ")
                            }
                        }
                        append("], \nbut now: ")
                        append(it.type.classifier).append("(").append(it)
                        append("), it !is ").append(typeLink.last())
                    })
                }
            }
        }

    return minType ?: Event::class
}


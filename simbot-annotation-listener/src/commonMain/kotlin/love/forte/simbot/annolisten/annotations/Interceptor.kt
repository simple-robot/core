package love.forte.simbot.annolisten.annotations

import love.forte.simbot.annolisten.EventInterceptorFactory
import kotlin.reflect.KClass

/**
 *
 * @author ForteScarlet
 */
public annotation class Interceptor(val factory: KClass<out EventInterceptorFactory>)

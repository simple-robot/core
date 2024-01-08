package love.forte.simbot.spring.utils

import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


internal fun ConfigurableListableBeanFactory.getTargetTypeSafely(beanName: String): Class<*>? {
    val type = kotlin.runCatching { AutoProxyUtils.determineTargetClass(this, beanName) }.getOrNull() ?: return null

    return if (ScopedObject::class.java.isAssignableFrom(type)) {
        return kotlin.runCatching {
            AutoProxyUtils.determineTargetClass(this, ScopedProxyUtils.getTargetBeanName(beanName))
        }.getOrElse { type }
    } else {
        type
    }
}

internal fun Method.getKotlinFunctionSafely(): KFunction<*>? {
    return kotlin.runCatching { kotlinFunction }.getOrNull()
}

internal inline fun <reified A : Annotation> Class<*>.selectMethodsSafely(): Map<Method, A>? {
    return runCatching {
        MethodIntrospector.selectMethods(this, MethodIntrospector.MetadataLookup { method ->
            AnnotatedElementUtils.findMergedAnnotation(method, A::class.java)
        })
    }.getOrNull()
}

internal inline fun <reified A : Annotation> AnnotatedElement.findMergedAnnotationSafely(): A? {
    return runCatching {
        AnnotatedElementUtils.findMergedAnnotation(this, A::class.java)
    }.getOrNull()
}

internal inline fun <reified A : Annotation> AnnotatedElement.findRepeatableMergedAnnotationSafely(): Set<A>? {
    return runCatching {
        AnnotatedElementUtils.findMergedRepeatableAnnotations(this, A::class.java)
    }.getOrNull()
}

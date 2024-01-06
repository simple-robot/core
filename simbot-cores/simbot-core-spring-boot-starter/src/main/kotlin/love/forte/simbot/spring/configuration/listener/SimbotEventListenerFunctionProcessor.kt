package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.application.Application
import love.forte.simbot.event.EventListener
import love.forte.simbot.quantcat.annotations.Listener
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.ConfigurationClassPostProcessor
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

/**
 * 用于通过 [Application] 构建 [EventListener] 的工厂函数。
 */
public fun interface EventListenerFactory {
    public fun create(application: Application): EventListener?
}

/**
 * 将所有 bean 中标记了 [Listener] 的函数解析为 [EventListenerFactory]
 *
 * @author ForteScarlet
 */
public open class SimbotEventListenerFunctionProcessor : ApplicationContextAware, ConfigurationClassPostProcessor() {
    private val processor = KFunctionEventListenerProcessor()

    private lateinit var applicationContext: ApplicationContext
    private lateinit var registry: BeanDefinitionRegistry

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        this.registry = registry
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val beanNames = beanFactory.getBeanNamesForType(Any::class.java)
        for (beanName in beanNames) {
            if (ScopedProxyUtils.isScopedTarget(beanName)) {
                continue
            }

            val beanType = beanFactory.getTargetTypeSafely(beanName) ?: continue

            beanFactory.processListener(beanName, beanType)
        }
    }


    private fun ConfigurableListableBeanFactory.processListener(beanName: String, beanType: Class<*>) {
        if (!AnnotationUtils.isCandidateClass(beanType, Listener::class.java)) {
            return
        }

        val annotatedMethods: Map<Method, Listener> = beanType.selectMethodsSafely() ?: return
        annotatedMethods.forEach { (method, listenerAnnotation) ->
            val eventListenerRegistrationDescription =
                resolveMethodToListener(beanName, method, listenerAnnotation, TODO())
                    ?: return@forEach

//            val beanDefinition = eventListenerRegistrationDescription.resolveToBeanDefinition()
            val beanDefinition = resolveToBeanDefinition(eventListenerRegistrationDescription)

            registry.registerBeanDefinition(generatedListenerBeanName(beanName, method), beanDefinition)
        }
    }

    private fun resolveMethodToListener(
        beanName: String, method: Method, listenerAnnotation: Listener,
        listenerProcessor: KFunctionEventListenerProcessor
    ): (() -> EventListenerFactory)? {

        TODO()
    }

    public companion object {
        private val logger = LoggerFactory.getLogger(SimbotEventListenerFunctionProcessor::class.java)
    }
}


private fun resolveToBeanDefinition(instanceSupplier: () -> EventListenerFactory): BeanDefinition {
    return BeanDefinitionBuilder.genericBeanDefinition(
        EventListenerFactory::class.java,
        instanceSupplier
    ).setPrimary(false).beanDefinition
}


private fun ConfigurableListableBeanFactory.getTargetTypeSafely(beanName: String): Class<*>? {
    val type = kotlin.runCatching { AutoProxyUtils.determineTargetClass(this, beanName) }.getOrNull() ?: return null

    return if (ScopedObject::class.java.isAssignableFrom(type)) {
        return kotlin.runCatching {
            AutoProxyUtils.determineTargetClass(this, ScopedProxyUtils.getTargetBeanName(beanName))
        }.getOrElse { type }
    } else {
        type
    }
}

private fun Method.getKotlinFunctionSafely(): KFunction<*>? {
    return kotlin.runCatching { kotlinFunction }.getOrNull()
}

private inline fun <reified A : Annotation> Class<*>.selectMethodsSafely(): Map<Method, A>? {
    return runCatching {
        MethodIntrospector.selectMethods(this, MethodIntrospector.MetadataLookup { method ->
            AnnotatedElementUtils.findMergedAnnotation(method, A::class.java)
        })
    }.getOrNull()
}

private fun generatedListenerBeanName(beanName: String, method: Method): String =
    "$beanName${method.toGenericString()}#GENERATED_LISTENER"

package love.forte.simbot.spring.configuration.listener

import love.forte.simbot.application.Application
import love.forte.simbot.event.EventListener
import love.forte.simbot.quantcat.annotations.ApplyBinder
import love.forte.simbot.quantcat.annotations.Listener
import love.forte.simbot.quantcat.common.binder.BinderManager
import love.forte.simbot.spring.utils.findMergedAnnotationSafely
import love.forte.simbot.spring.utils.getKotlinFunctionSafely
import love.forte.simbot.spring.utils.getTargetTypeSafely
import love.forte.simbot.spring.utils.selectMethodsSafely
import org.slf4j.LoggerFactory
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.ConfigurationClassPostProcessor
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method
import kotlin.reflect.KFunction

/**
 * 用于通过 [Application] 注册 [EventListener] 的函数接口。
 */
public fun interface SimbotEventListenerResolver {
    public fun resolve(application: Application)
}

/**
 * 将所有 bean 中标记了 [Listener] 的函数解析为 [SimbotEventListenerResolver]
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
        beanFactory.beanNamesIterator.asSequence()
            .filter { !ScopedProxyUtils.isScopedTarget(it) }
            .forEach { beanName ->
                val beanType = beanFactory.getTargetTypeSafely(beanName) ?: return@forEach

                beanFactory.processListener(beanName, beanType)
            }
    }


    private fun ConfigurableListableBeanFactory.processListener(
        beanName: String,
        beanType: Class<*>,
    ) {
        if (!AnnotationUtils.isCandidateClass(beanType, Listener::class.java)) {
            return
        }

        val annotatedMethods = beanType.selectMethodsSafely<Listener>()?.takeIf { it.isNotEmpty() } ?: return

        logger.debug("Resolve candidate class {} bean named {} with any @Listener methods", beanType, beanName)

        annotatedMethods.forEach { (method, listenerAnnotation) ->
            val function = method.getKotlinFunctionSafely() ?: return@forEach

            val applyBinder = method.findMergedAnnotationSafely<ApplyBinder>()

            val eventListenerResolverDescription =
                resolveMethodToListener(beanName, function, listenerAnnotation, applyBinder)

            val beanDefinition = resolveToBeanDefinition(eventListenerResolverDescription)
            val beanDefinitionName = generatedListenerBeanName(beanName, method)

            logger.debug("Generate event listener resolver bean definition {} named {}", beanDefinition, beanDefinitionName)

            registry.registerBeanDefinition(beanDefinitionName, beanDefinition)
        }
    }

    private fun ConfigurableListableBeanFactory.resolveMethodToListener(
        beanName: String,
        function: KFunction<*>,
        listenerAnnotation: Listener,
        applyBinder: ApplyBinder?,
    ): (() -> SimbotEventListenerResolver) {
        return {
            val binderManagerInstance = getBean(BinderManager::class.java)
            processor.process(
                beanName,
                function,
                listenerAnnotation,
                applyBinder,
                applicationContext,
                binderManagerInstance
            )
        }
    }

    private fun resolveToBeanDefinition(instanceSupplier: () -> SimbotEventListenerResolver): BeanDefinition {
        return BeanDefinitionBuilder.genericBeanDefinition(
            SimbotEventListenerResolver::class.java,
            instanceSupplier
        ).setPrimary(false)
            .beanDefinition
    }

    public companion object {
        private val logger = LoggerFactory.getLogger(SimbotEventListenerFunctionProcessor::class.java)
    }
}


private fun generatedListenerBeanName(beanName: String, method: Method): String =
    "$beanName${method.toGenericString()}#GENERATED_LISTENER"

package love.forte.simbot.spring.configuration.binder

import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.ParameterBinderResult
import org.springframework.beans.factory.BeanFactory
import kotlin.reflect.KParameter


/**
 *
 * @author ForteScarlet
 */
public class SpringAutoInjectBinderFactory(
    private val factory: BeanFactory
) : ParameterBinderFactory {

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val parameter = context.parameter
        // skip instance
        if (parameter.kind == KParameter.Kind.INSTANCE) return ParameterBinderResult.empty()



        TODO()
    }
}

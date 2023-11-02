package love.forte.simbot.application

import love.forte.simbot.function.toConfigurerFunction


/**
 *
 */
public suspend inline fun <A : Application, C : ApplicationConfigurationBuilder, L : ApplicationLauncher<A>> application(
    factory: ApplicationFactory<A, C, L>,
    crossinline configurer: ApplicationFactoryConfigurer<C>.() -> Unit
): A {
    return factory.create(toConfigurerFunction(configurer)).launch()
}

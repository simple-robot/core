package love.forte.simbot.core.application

import love.forte.simbot.application.*
import love.forte.simbot.component.ComponentFactoriesConfigurator
import love.forte.simbot.function.ConfigurerFunction
import love.forte.simbot.function.invokeWith
import love.forte.simbot.plugin.PluginFactoriesConfigurator


public object Simple :
    ApplicationFactory<SimpleApplication, SimpleApplicationConfigurationBuilder, SimpleApplicationLauncher> {
    override fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<SimpleApplicationConfigurationBuilder>>): SimpleApplicationLauncher {
        val simpleConfigurer = SimpleApplicationFactoryConfigurer()
        configurer.invokeWith(simpleConfigurer)


        TODO("Not yet implemented")
    }
}

private class SimpleApplicationFactoryConfigurer(
    public override val configConfigurers: MutableList<ConfigurerFunction<SimpleApplicationConfigurationBuilder>> = mutableListOf(),
    public override val componentFactoriesConfigurator: ComponentFactoriesConfigurator = ComponentFactoriesConfigurator(),
    public override val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : AbstractApplicationFactoryConfigurer<SimpleApplicationConfigurationBuilder>(
    configConfigurers,
    componentFactoriesConfigurator,
    pluginFactoriesConfigurator
)

public interface SimpleApplicationConfiguration : ApplicationConfiguration {
    // properties?
}

public class SimpleApplicationConfigurationBuilder : ApplicationConfigurationBuilder() {
    override fun build(): SimpleApplicationConfiguration {

        TODO()
    }
}


public class SimpleApplicationLauncher : ApplicationLauncher<SimpleApplication> {
    override suspend fun launch(): SimpleApplication {
        TODO("Not yet implemented")
    }
}

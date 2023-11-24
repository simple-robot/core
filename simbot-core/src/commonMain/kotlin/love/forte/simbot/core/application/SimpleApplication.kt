package love.forte.simbot.core.application

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationLauncher


public interface SimpleApplication : Application {
    override val configuration: SimpleApplicationConfiguration
}

// TODO

public interface SimpleApplicationConfiguration : ApplicationConfiguration {
    // properties?
}

public interface SimpleApplicationLauncher : ApplicationLauncher<SimpleApplication>

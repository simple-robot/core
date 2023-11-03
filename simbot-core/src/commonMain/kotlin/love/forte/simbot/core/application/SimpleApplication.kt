package love.forte.simbot.core.application

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.Components
import love.forte.simbot.application.Plugins
import love.forte.simbot.event.EventDispatcher
import kotlin.coroutines.CoroutineContext


public class SimpleApplication : Application {
    override val configuration: ApplicationConfiguration
        get() = TODO("Not yet implemented")
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val eventDispatcher: EventDispatcher
        get() = TODO("Not yet implemented")
    override val components: Components
        get() = TODO("Not yet implemented")
    override val plugins: Plugins
        get() = TODO("Not yet implemented")

}

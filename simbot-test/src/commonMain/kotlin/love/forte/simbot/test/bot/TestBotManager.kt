package love.forte.simbot.test.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.bot.*
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.common.coroutines.linkTo
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.NoSuchComponentException
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.test.component.TestComponent


/**
 * 用于测试的 [BotManager] 实现。
 *
 * @author ForteScarlet
 */
public open class TestBotManager(
    private val component: TestComponent,
    private val configuration: TestBotManagerConfiguration,
    override val job: Job
) : JobBasedBotManager() {
    private val bots = createConcurrentQueue<TestBot>()

    override fun all(): Sequence<TestBot> = bots.asSequence()

    override fun get(id: ID): TestBot {
        return bots.find { it.id == id } ?: throw NoSuchBotException("id=$id")
    }

    override fun configurable(configuration: SerializableBotConfiguration): Boolean =
        configuration is TestBotConfiguration

    override fun register(configuration: SerializableBotConfiguration): TestBot {
        val c = configuration as? TestBotConfiguration ?: throw UnsupportedBotConfigurationException()

        var coroutineContext = c.coroutineContext
        val job = coroutineContext[Job]
        if (job == null) {
            val newJob = SupervisorJob(this.job)
            coroutineContext += newJob
        } else {
            job.linkTo(this.job)
        }

        return TestBot(component, c, coroutineContext)
    }

    public companion object Factory : BotManagerFactory<TestBotManager, TestBotManagerConfiguration> {
        override val key: BotManagerFactory.Key = object : BotManagerFactory.Key {}

        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<TestBotManagerConfiguration>
        ): TestBotManager {
            val component = context.components.find { it is TestComponent } as? TestComponent
                ?: throw NoSuchComponentException(TestComponent::class.toString())

            val config = TestBotManagerConfiguration().invokeBy(configurer)
            val job = SupervisorJob(config.job)

            return TestBotManager(component, config, job)
        }
    }
}

/**
 * [TestBotManager] 的配置类。
 */
public open class TestBotManagerConfiguration {
    public var job: Job? = null
}

package love.forte.simbot.test.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.bot.*
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.Component
import love.forte.simbot.test.component.TestComponent
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于测试的 [Bot] 实现。
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class TestBot(
    override val component: Component,
    public val configuration: TestBotConfiguration,
    coroutineContext: CoroutineContext
) : JobBasedBot() {
    final override val coroutineContext: CoroutineContext
    final override val job: Job

    init {
        val j = coroutineContext[Job]
        val c = coroutineContext.minusKey(Job)
        this.job = SupervisorJob(j)
        this.coroutineContext = c
    }

    override val id: ID = configuration.id.ID
    override val name: String = configuration.name

    override fun isMe(id: ID): Boolean = this.id == id

    override suspend fun start() {
        isStarted = true
    }

    override val guildRelation: GuildRelation? = null
    override val groupRelation: GroupRelation? = null
    override val contactRelation: ContactRelation? = null
}

/**
 * [TestBot] 的配置类。
 */
@Serializable
@SerialName(TestComponent.ID_VALUE)
public data class TestBotConfiguration(
    var id: String,
    var name: String,
) : SerializableBotConfiguration() {
    @Transient
    var coroutineContext: CoroutineContext = EmptyCoroutineContext
}

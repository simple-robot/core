package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 一个 **消息链**。
 * 消息链 [Messages] 是一组 [Message.Element] 的表现。
 *
 * 消息链是**不可变的**。它通过 [plus] 与其他消息元素或消息链重新组合为新的消息链。
 *
 */
public sealed interface Messages : Message, Iterable<Message.Element> {
    /**
     * 得到当前消息链中的元素迭代器。
     */
    override fun iterator(): Iterator<Message.Element>

    /**
     * 获取当前消息链中的元素数量。
     */
    public val size: Int

    /**
     * 判断当前消息链是否为空。
     */
    public fun isEmpty(): Boolean

    /**
     * 以当前消息链为准构建一个 [List] 类型的瞬时副本。
     */
    public fun toList(): List<Message.Element>

    /**
     * 合并一个 [Message.Element] 并得到新的消息链。
     */
    public operator fun plus(element: Message.Element): Messages

    /**
     * 合并一个消息集并得到新的消息链。
     */
    public operator fun plus(messages: Iterable<Message.Element>): Messages

    public companion object {
        /**
         * 由标准API默认提供的消息类型的序列化信息。
         */
        public val standardSerializersModule: SerializersModule = SerializersModule {
            polymorphic(Message.Element::class) {
                subclass(Text.serializer())
                subclass(At.serializer())
                subclass(AtAll.serializer())
                // images
                subclass(OfflineByteArrayImage.serializer())
                subclass(SimpleOfflineResourceImage.serializer())
                subclass(RemoteIDImage.serializer())

                subclass(Emoji.serializer())
                subclass(Face.serializer())

                resolveStandardSerializersModule()
            }
        }


        /**
         * 可用于 [Messages] 进行序列化的 [KSerializer].
         *
         * 会将 [Messages] 视为 [Message.Element] 列表进行序列化。
         */
        @JvmStatic
        @get:JvmName("serializer")
        public val serializer: KSerializer<Messages>
            get() = MessagesSerializer


        private object MessagesSerializer : KSerializer<Messages> {
            private val delegate = ListSerializer(PolymorphicSerializer(Message.Element::class))
            override val descriptor: SerialDescriptor get() = delegate.descriptor
            override fun deserialize(decoder: Decoder): Messages = delegate.deserialize(decoder).toMessages()
            override fun serialize(encoder: Encoder, value: Messages) {
                delegate.serialize(encoder, value.toList())
            }
        }

        /**
         * 为一个元素创建 [Messages] 对象。
         *
         * @param element 消息元素。
         * @return 包含该元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(element: Message.Element): Messages = messagesOf(element)

        /**
         * 为一个或多个元素创建 [Messages] 对象。
         *
         * @param elements 可变数量的消息元素。
         * @return 包含这些元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(vararg elements: Message.Element): Messages = messagesOf(elements = elements)

        /**
         * 创建一个空的 [Messages] 对象。
         *
         * @return 一个不包含任何元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun empty(): Messages = emptyMessages()

        /**
         * 为一个可迭代对象创建 [Messages] 对象。
         *
         * @param iterable 可迭代的消息元素。
         * @return 包含这些元素的 [Messages] 对象。
         */
        @JvmStatic
        public fun of(iterable: Iterable<Message.Element>): Messages = iterable.toMessages()
    }
}


internal expect fun PolymorphicModuleBuilder<Message.Element>.resolveStandardSerializersModule()


private object EmptyMessages : Messages {
    override fun iterator(): Iterator<Message.Element> = toList().iterator()
    override val size: Int get() = 0
    override fun isEmpty(): Boolean = true
    override fun toList(): List<Message.Element> = emptyList()

    override fun plus(element: Message.Element): Messages {
        return messagesOf(element)
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        return when (messages) {
            is EmptyMessages -> EmptyMessages
            is Messages -> messages
            else -> messages.toMessages()
        }
    }

    override fun toString(): String = "EmptyMessages"

    override fun equals(other: Any?): Boolean {
        return other === EmptyMessages
    }
}

private class SingleElementMessages(private val element: Message.Element) : Messages {
    override fun iterator(): Iterator<Message.Element> = toList().iterator()

    override val size: Int
        get() = 1

    override fun isEmpty(): Boolean = false

    override fun toList(): List<Message.Element> = listOf(element)

    override fun plus(element: Message.Element): Messages {
        return ListMessages(listOf(this.element, element))
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        if (messages is EmptyMessages) return this
        if (messages is Collection) {
            if (messages.isEmpty()) {
                return this
            }

            return ListMessages(buildList(messages.size + 1) {
                add(this@SingleElementMessages.element)
                addAll(messages)
            })
        }

        return ListMessages(buildList {
            add(this@SingleElementMessages.element)
            addAll(messages)
        })
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is SingleElementMessages) return false

        return element == other.element
    }

    override fun hashCode(): Int = element.hashCode()

    override fun toString(): String = "Messages([$element])"
}

/**
 * 基于 [List] 的 [Messages] 实现。[list] 中至少存在2个元素，不会为空。
 *
 */
private class ListMessages(private val list: List<Message.Element>) : Messages {
    override fun iterator(): Iterator<Message.Element> = list.iterator()

    override val size: Int
        get() = list.size

    override fun isEmpty(): Boolean = list.isEmpty() // always true

    override fun toList(): List<Message.Element> = list.toList()

    override fun plus(element: Message.Element): Messages {
        return ListMessages(list + element)
    }

    override fun plus(messages: Iterable<Message.Element>): Messages {
        if (messages is EmptyMessages) return this
        if (messages is Collection && messages.isEmpty()) return this

        return ListMessages(list + messages)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ListMessages) return false

        return list == other.list
    }

    override fun hashCode(): Int = list.hashCode()

    override fun toString(): String = "Messages($list)"

}

/**
 * 返回一个空的 [Messages] 对象
 *
 * @return 空的Messages对象
 */
public fun emptyMessages(): Messages = EmptyMessages

/**
 * 创建一个单元素的 [Messages] 对象
 *
 * @param element 单个消息元素
 * @return 单元素的 [Messages] 对象
 */
public fun messagesOf(element: Message.Element): Messages = SingleElementMessages(element)

/**
 * 根据输入的消息元素数组创建 [Messages] 对象
 *
 * @param elements 消息元素数组
 * @return 根据输入创建的 [Messages] 对象
 */
public fun messagesOf(vararg elements: Message.Element): Messages {
    return when (elements.size) {
        0 -> emptyMessages()
        1 -> messagesOf(elements[0])
        else -> ListMessages(elements.toList())
    }
}

/**
 * 将元素集转换为 [Messages] 对象
 *
 * @return 根据输入的元素列表创建的 [Messages] 对象
 */
public fun Iterable<Message.Element>.toMessages(): Messages {
    return when (this) {
        is Collection -> when {
            isEmpty() -> emptyMessages()
            size == 1 -> messagesOf(first())
            else -> ListMessages(this.toList())
        }

        else -> {
            val iterator = iterator()
            val hasValue = iterator.hasNext()
            if (!hasValue) {
                return emptyMessages()
            }

            val firstValue = iterator.next()
            val hasSecondValue = iterator.hasNext()
            if (!hasSecondValue) {
                return messagesOf(firstValue)
            }

            val list = buildList {
                add(firstValue)
                for (element in iterator) {
                    add(element)
                }
            }

            ListMessages(list)
        }
    }
}

/**
 * Builds a list of Messages using the provided container and block.
 *
 * @param container The mutable list that contains the message elements. Default is an empty list.
 * @param block The lambda expression where the MessagesBuilder functions are called to populate the message elements.
 * @return The built Messages object.
 */
public inline fun buildMessages(
    container: MutableList<Message.Element> = mutableListOf(),
    block: MessagesBuilder.() -> Unit
): Messages = MessagesBuilder.create(container).apply(block).build()


/**
 * 一个用于动态构建 [Messages] 的构建器。
 */
public class MessagesBuilder private constructor(private val container: MutableList<Message.Element> = mutableListOf()) {
    public companion object {

        /**
         * Creates a new instance of MessagesBuilder.
         *
         * @param container The list of Message.Element objects. Defaults to an empty mutable list if not specified.
         * @return An instance of MessagesBuilder.
         */
        @JvmStatic
        @JvmOverloads
        public fun create(container: MutableList<Message.Element> = mutableListOf()): MessagesBuilder =
            MessagesBuilder(container)
    }

    /**
     * Add an element to the [MessagesBuilder] container.
     *
     * @param element the element to be added
     * @return the updated MessagesBuilder instance
     */
    public fun add(element: Message.Element): MessagesBuilder = apply { container.add(element) }

    /**
     * Adds the given text to the [MessagesBuilder] container.
     *
     * @param text the text to add to the container
     * @return the updated MessagesBuilder object
     */
    public fun add(text: String): MessagesBuilder = apply { container.add(text.toText()) }

    /**
     * Build method.
     *
     * This method constructs and returns a Messages object using the container.
     *
     * @return The constructed Messages object.
     */
    public fun build(): Messages = container.toMessages()
}

/**
 * Encodes the given [messages] object to a String representation using the StringFormat.
 *
 * @param messages The Messages object that needs to be encoded.
 * @return The encoded String representation of the Messages object.
 */
public fun StringFormat.encodeMessagesToString(messages: Messages): String = encodeToString(Messages.serializer, messages)

/**
 * Decodes a string representation of Messages using the provided StringFormat.
 *
 * @param string The string representation of Messages to decode.
 * @return The deserialized Messages object.
 */
public fun StringFormat.decodeMessagesFromString(string: String): Messages = decodeFromString(Messages.serializer, string)

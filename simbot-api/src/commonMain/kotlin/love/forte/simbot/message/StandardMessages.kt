package love.forte.simbot.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.id.ID
import love.forte.simbot.message.At.Companion.equals
import love.forte.simbot.message.At.Companion.hashCode
import love.forte.simbot.message.Text.Companion.of
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * 一些由核心提供的标准 [Message.Element] 类型。
 * 标准消息中，仅提供如下实现：
 * - [纯文本消息][PlainText]
 * - [AT消息][At]
 * - [图片消息][Image]
 * - [表情消息][Face]
 * - [emoji][Emoji]
 *
 */
public sealed interface StandardMessage : Message.Element

//region Text
/**
 * 纯文本消息。代表一段只存在[文本][text]的消息。
 *
 * 实际上绝大多数情况下，都不需要独立实现 [PlainText] 类型，
 * [PlainText] 提供了最基础的实现类型 [Text]。
 *
 * @see Text
 */
public interface PlainText : StandardMessage {
    /**
     * 文本内容
     */
    public val text: String
}

/**
 * 一个文本消息 [Text]。[Text] 是 [PlainText] 基础实现类型。
 *
 * 文本消息可以存在多个，但是对于不同平台来讲，有可能存在差异。
 * 部分平台会按照正常的方式顺序排列消息，而有的则会组合消息列表中的所有文本消息为一个整体。
 *
 * @see toText
 * @see Text
 * @see of
 */
@Serializable
@SerialName("m.std.text")
public class Text private constructor(override val text: String) : PlainText {
    override fun toString(): String = "Text($text)"
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Text) return false
        return text == other.text
    }

    override fun hashCode(): Int = text.hashCode()

    public companion object {
        private val empty = Text("")

        /**
         * Creates a new instance of Text with the given text.
         *
         * @param text the text to create the Text instance with.
         * @return a new instance of Text with the given text.
         */
        @JvmStatic
        public fun of(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }
    }
}

/**
 * 将一个字符串转化为 [Text].
 * ```kotlin
 * val text: Text = "mua".toText()
 * ```
 */
public fun String.toText(): Text = Text.of(this)

/**
 * 得到一个空的 [Text].
 *
 */
@JsName("emptyText")
public fun Text(): Text = Text.of("")

/**
 * 构建一个 [Text].
 *
 * ```kotlin
 * val text: Text = Text { "Hello" }
 * ```
 *
 */
public inline fun Text(block: () -> String): Text = block().toText()
//endregion

//region At
/**
 * 代表一个描述“提及”的消息。常见表现形式即为 [At]。
 *
 * @see At
 * @see AtAll
 *
 */
public interface MentionMessage : StandardMessage

/**
 * 一个艾特消息。
 *
 * 是针对“提及”的常见标准表现形式。
 * 默认情况下 [At] 表现为针对某个用户的提及（即默认的 [type] 为 `"user"`）。
 *
 * 当同一个组件中可能存在多种类型的提及时，可以选择通过约定不同的 [type] 来区分，
 * 也可以选择实现更多扩展消息元素类型来做区分。
 *
 */
@Serializable
@SerialName("m.std.at")
public data class At @JvmOverloads constructor(
    public val target: ID,
    @SerialName("atType") public val type: String = DEFAULT_AT_TYPE,

    /**
     * 这个at在原始数据中或者原始事件中的样子。默认情况下，是字符串 '@[target]'。
     * 此值通常仅供参考，且不会参与 [equals] 于 [hashCode] 的计算。
     */
    public val originContent: String = "@$target",
) : MentionMessage {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is At) return false
        return other.target == target && other.type == type
    }


    override fun hashCode(): Int {
        return 31 * (target.hashCode() + type.hashCode())
    }

    override fun toString(): String {
        return "At(target=$target, type=$type, originContent=$originContent)"
    }

    public companion object {
        public const val DEFAULT_AT_TYPE: String = "user"
    }
}

/**
 * 一个“通知所有”的消息。
 * 如果在同一组件环境下的 “通知所有” 有多种表现形式，可考虑扩展更多消息元素类型。
 */
@Serializable
@SerialName("m.std.atAll")
public data object AtAll : MentionMessage

//endregion

//region Description
// TODO
/**
 * 一个图片消息元素类型。
 *
 */
public interface ImageMessage : StandardMessage

// 离线图片？
// 远程图片？

/**
 * 一个离线图片消息元素类型。
 *
 * “离线图片”即代表一个在当前机器中本地存在的图片资源。
 * 它可能是内存中的一段二进制数据，或本地文件系统中的某个文件。
 *
 * “离线”主要表示此图片并未上传到某个目标平台中，也没有与某个远程服务器互相对应的唯一标识。
 *
 * [OfflineImage] 提供一些默认实现，其中最通用的为 [OfflineByteArrayImage]，它直接针对原始数据进行包装。
 *
 * 在 JVM 平台下会额外提供更多基于文件系统的扩展类型，例如通过 `File` 或 `Path` 来构建一个 [OfflineImage]。
 *
 */
public interface OfflineImage : ImageMessage {

    // TODO 拆出 [Resource] ?

    /**
     * 得到图片的二进制数据
     */
    @Throws(Exception::class)
    public fun data(): ByteArray

    public companion object {
        /**
         * Creates an [OfflineImage] from a byte array.
         *
         * @param data the byte array containing the image data
         * @return the [OfflineImage] created from the byte array
         */
        @JvmStatic
        public fun ofBytes(data: ByteArray): OfflineImage = OfflineByteArrayImage(data)
    }
}

/**
 * 直接针对一个 [ByteArray] 进行包装的 [OfflineImage] 实现。
 */
@Serializable
@SerialName("m.std.img.offline.bytes")
public data class OfflineByteArrayImage(private val data: ByteArray) : OfflineImage {
    override fun data(): ByteArray = data

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflineByteArrayImage) return false

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}

/**
 * 一个远程图片消息元素类型。
 *
 * 远程图片通常是通过事件推送、主动上传等手段得到的、有与某个远程服务器互相对应的唯一标识的图片。
 * 这个标识可能是一个ID，或一个访问链接。
 *
 * @see RemoteIDImage
 */
public interface RemoteImage : ImageMessage {
    /**
     * 在远程服务器上的唯一标识。
     *
     * 可能是一个ID，也可能是一个资源定位符（即图片链接）。
     */
    public val id: ID
}

/**
 * 一个仅基于 [ID] 的 [RemoteImage] 基础实现。
 */
@Serializable
@SerialName("m.std.img.remote.id")
public data class RemoteIDImage(override val id: ID) : RemoteImage


//endregion

//region Emoticon
/**
 * 表示某种表情符号的消息元素类型。
 * 常见表现形式有某平台的系统表情或一定范围内的 `emoji` 表情。
 *
 */
public interface EmoticonMessage : StandardMessage

/**
 * 一个 `emoji` 表情。
 *
 * [Emoji] 主要服务于那些只能提供指定范围内 `emoji` 表情的场景，
 * 例如针对某个消息的 `reaction`。
 *
 * 现代绝大多数的平台中，如果希望在普通的文本消息中插入 `emoji` 不需要使用特殊的消息类型，
 * 仅需要添加在字符串中即可。
 *
 */
@Serializable
@SerialName("m.std.emoji")
public data class Emoji(public val id: ID) : EmoticonMessage

/**
 * 一个表情。一般代表平台提供的自带系统表情。
 */
@Serializable
@SerialName("m.std.face")
public data class Face(public val id: ID) : EmoticonMessage
//endregion


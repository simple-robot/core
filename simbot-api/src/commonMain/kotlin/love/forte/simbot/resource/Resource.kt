@file:JvmName("Resources")
@file:JvmMultifileClass

package love.forte.simbot.resource

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 一个**资源**。
 *
 * 用于描述一个可以被读取字节数据（[data]）的资源。
 * 通常代表一些二进制数据或本地文件资源。
 *
 * ### 序列化
 *
 * [Resource] 提供了一个基于 [Base64] 进行序列化操作的 [ResourceBase64Serializer]。
 *
 * @author ForteScarlet
 */
public interface Resource {
    /**
     * 读取此资源的字节数据。
     *
     */
    @Throws(Exception::class)
    public fun data(): ByteArray
}


/**
 * 通过提供的 [ByteArray] 直接构建一个 [Resource]。
 *
 * @return Resource object representing the ByteArray data.
 */
@JvmName("valueOf")
public fun ByteArray.toResource(): ByteArrayResource = ByteArrayResourceImpl(this)

/**
 * 直接使用 [ByteArray] 作为 [data] 结果的 [Resource] 实现。
 *
 * @author forte
 */
public interface ByteArrayResource : Resource {
    /**
     * 获取到字节数组结果。
     */
    override fun data(): ByteArray
}

/**
 * 基于 [Base64] 的 [Resource] 序列化器。
 */
@ExperimentalEncodingApi
public object ResourceBase64Serializer : KSerializer<Resource> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("B64Resource", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Resource {
        val b64 = decoder.decodeString()
        return Base64.decode(b64).toResource()
    }

    override fun serialize(encoder: Encoder, value: Resource) {
        encoder.encodeString(Base64.encode(value.data()))
    }
}


/**
 * 直接基于 [ByteArray] 的 [Resource] 实现。
 *
 * 被包装使用的 [ByteArray] 不会发生拷贝，因此请避免修改原始的数组或 [data] 得到的数组。
 *
 */
private data class ByteArrayResourceImpl(private val raw: ByteArray) : ByteArrayResource {
    override fun data(): ByteArray = raw

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayResourceImpl) return false

        if (!raw.contentEquals(other.raw)) return false

        return true
    }

    override fun hashCode(): Int {
        return raw.contentHashCode()
    }
}

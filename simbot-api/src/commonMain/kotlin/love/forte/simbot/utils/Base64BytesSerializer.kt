package love.forte.simbot.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * This class represents a serializer for converting a ByteArray to and from Base64 string representation.
 * It implements the KSerializer interface.
 */
public object Base64BytesSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Base64Bytes", PrimitiveKind.STRING)

    @ExperimentalEncodingApi
    override fun deserialize(decoder: Decoder): ByteArray {
        val str = decoder.decodeString()
        return Base64.decode(str)
    }

    @ExperimentalEncodingApi
    override fun serialize(encoder: Encoder, value: ByteArray) {
        encoder.encodeString(Base64.encode(value))
    }
}

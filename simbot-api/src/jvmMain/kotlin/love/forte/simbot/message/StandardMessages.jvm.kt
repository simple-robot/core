package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.readBytes

/**
 * 基于 [File] 的 [OfflineImage] 实现。
 *
 * [file] 的序列化会通过 [File.getName] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.file")
public data class OfflineFileImage(@Serializable(FileSerializer::class) public val file: File) : OfflineImage {
    @Throws(IOException::class)
    override fun data(): ByteArray =
        file.readBytes()
}

internal object FileSerializer : KSerializer<File> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("File", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): File {
        val pathname = decoder.decodeString()
        return File(pathname)
    }

    override fun serialize(encoder: Encoder, value: File) {
        encoder.encodeString(value.name)
    }
}

/**
 * 基于 [Path] 的 [OfflineImage] 实现。
 *
 * [path] 的序列化会通过 [File.pathString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.path")
public data class OfflinePathImage(@Serializable(PathSerializer::class) public val path: Path) : OfflineImage {
    @Throws(IOException::class)
    override fun data(): ByteArray =
        path.readBytes()
}

internal object PathSerializer : KSerializer<Path> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Path", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Path {
        val path = decoder.decodeString()
        return Path(path)
    }

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.pathString)
    }

}


/**
 * 基于 [URI] 的 [OfflineImage] 实现。
 *
 * [uri] 的序列化会通过 [URI.toString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.uri")
public data class OfflineURIImage(@Serializable(URISerializer::class) public val uri: URI) : OfflineImage {

    /**
     * Read bytes from [uri] (convert to [java.net.URL]).
     *
     * @throws java.net.MalformedURLException see [URI.toURL]
     */
    @Throws(IOException::class)
    override fun data(): ByteArray =
        uri.toURL().readBytes()
}


internal object URISerializer : KSerializer<URI> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URI {
        val str = decoder.decodeString()
        return URI.create(str)
    }

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value.toString())
    }

}

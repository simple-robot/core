@file:JvmName("StandardMessages")
@file:JvmMultifileClass
package love.forte.simbot.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.message.OfflineFileImage.Companion.toOfflineFileImage
import love.forte.simbot.message.OfflinePathImage.Companion.toOfflinePathImage
import love.forte.simbot.message.OfflineURLImage.Companion.toOfflineImage
import love.forte.simbot.resource.*
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.readBytes

/**
 * 将 [Resource] 转化为 [OfflineResourceImage]。
 *
 * 如果 [Resource] 类型为 [FileResource]、[PathResource]、[URLResource]，
 * 则会分别对应地得到 [OfflineFileImage]、[OfflinePathImage]、[OfflineURLImage]，
 *
 * 否则将会使用 [SimpleOfflineResourceImage]。
 *
 */
public actual fun Resource.toOfflineResourceImage(): OfflineResourceImage {
    return when (this) {
        is FileResource -> toOfflineFileImage()
        is PathResource -> toOfflinePathImage()
        is URLResource -> toOfflineImage()
        else -> SimpleOfflineResourceImage(this)
    }
}

/**
 * 基于 [File] 的 [OfflineImage] 实现。
 *
 * [file] 的序列化会通过 [File.getName] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.file")
public class OfflineFileImage private constructor(@Serializable(FileSerializer::class) public val file: File) :
    OfflineResourceImage {
    public companion object {
        /**
         * Converts a [File] to an [OfflineFileImage].
         *
         * @return The [OfflineFileImage] representation of the File.
         */
        @JvmStatic
        @JvmName("of")
        public fun File.toOfflineImage(): OfflineFileImage =
            OfflineFileImage(this)

        /**
         * Converts a [FileResource] to an [OfflineFileImage].
         *
         * @return The converted [OfflineFileImage].
         */
        @JvmStatic
        @JvmName("of")
        public fun FileResource.toOfflineFileImage(): OfflineFileImage =
            OfflineFileImage(file).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: FileResource? = null

    override val resource: FileResource
        get() = _resource ?: file.toResource().also { _resource = it }

    @Throws(IOException::class)
    override fun data(): ByteArray = file.readBytes()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflineFileImage) return false

        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

    override fun toString(): String {
        return "OfflineFileImage(file=$file)"
    }
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
 * [path] 的序列化会通过 [Path.pathString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.path")
public data class OfflinePathImage(@Serializable(PathSerializer::class) public val path: Path) : OfflineResourceImage {
    public companion object {
        /**
         * Converts a [Path] object to an [OfflinePathImage] object representing an offline image.
         *
         * @return An [OfflinePathImage] object representing the offline image.
         */
        @JvmStatic
        @JvmName("of")
        public fun Path.toOfflineImage(): OfflinePathImage = OfflinePathImage(this)

        /**
         * Converts a [PathResource] to an [OfflinePathImage].
         *
         * @return The converted [OfflinePathImage].
         */
        @JvmStatic
        @JvmName("of")
        public fun PathResource.toOfflinePathImage(): OfflinePathImage =
            OfflinePathImage(path).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: PathResource? = null

    override val resource: PathResource
        get() = _resource ?: path.toResource().also {
            _resource = it
        }

    @Throws(IOException::class)
    override fun data(): ByteArray = path.readBytes()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfflinePathImage) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun toString(): String {
        return "OfflinePathImage(path=$path)"
    }
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
 * [url] 的序列化会通过 [URL.toString] 作为字符串进行。
 *
 */
@Serializable
@SerialName("m.std.img.offline.url")
public data class OfflineURLImage(@Serializable(URLSerializer::class) public val url: URL) : OfflineResourceImage {
    public companion object {

        /**
         * Converts the [URL] object to an [OfflineURLImage] object representing an offline image.
         *
         * @return An OfflineURLImage object representing the offline image.
         */
        @JvmStatic
        @JvmName("of")
        public fun URL.toOfflineImage(): OfflineURLImage = OfflineURLImage(this)

        /**
         * Converts the [URI] object to an [OfflineURLImage] object representing an offline image.
         *
         * @throws  IllegalArgumentException
         * If this URL is not absolute. See [URI.toURL]
         *
         * @throws  MalformedURLException
         * If a protocol handler for the URL could not be found,
         * or if some other error occurred while constructing the URL.
         * See [URI.toURL]
         */
        @JvmStatic
        @JvmName("of")
        @Throws(MalformedURLException::class)
        public fun URI.toOfflineImage(): OfflineURLImage = OfflineURLImage(toURL())

        @JvmStatic
        @JvmName("of")
        public fun URLResource.toOfflineImage(): OfflineURLImage =
            OfflineURLImage(url).also { image ->
                image._resource = this
            }
    }

    @Transient
    private var _resource: URLResource? = null

    override val resource: URLResource
        get() = _resource ?: url.toResource().also {
            _resource = it
        }

    /**
     * Read bytes from [url].
     *
     * @throws IOException see [URL.readBytes]
     */
    @Throws(IOException::class)
    override fun data(): ByteArray = url.readBytes()
}

internal object URLSerializer : KSerializer<URL> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URL {
        val str = decoder.decodeString()
        return URL(str)
    }

    override fun serialize(encoder: Encoder, value: URL) {
        encoder.encodeString(value.toString())
    }
}

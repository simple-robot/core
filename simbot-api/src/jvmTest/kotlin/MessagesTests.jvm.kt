import love.forte.simbot.message.MessagesBuilder
import love.forte.simbot.message.OfflineFileImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflinePathImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineURLImage.Companion.toOfflineImage
import love.forte.simbot.resource.toResource
import java.io.File
import java.net.URI
import java.net.URL
import kotlin.io.path.Path

internal actual fun MessagesBuilder.addIntoMessages() {
    add(File("").toResource().toOfflineImage())
    add(Path("").toResource().toOfflineImage())
    add(File("").toOfflineImage())
    add(Path("").toOfflineImage())
    add(URL("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toOfflineImage())
    add(URL("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toResource().toOfflineImage())
    add(URI.create("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toResource().toOfflineImage())
    add(URI.create("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toOfflineImage())
}

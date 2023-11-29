package love.forte.simbot.message

import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

internal actual fun PolymorphicModuleBuilder<Message.Element>.resolveStandardSerializersModule() {
    subclass(OfflineFileImage.serializer())
    subclass(OfflinePathImage.serializer())
    subclass(OfflineURLImage.serializer())
}

package love.forte.simbot.message

import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

internal actual fun PolymorphicModuleBuilder<Message.Element>.resolvePlatformStandardSerializers() {
    subclass(OfflineFileImage.serializer())
    subclass(OfflinePathImage.serializer())
    subclass(OfflineURLImage.serializer())
}

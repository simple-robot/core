package love.forte.simbot.message

import kotlinx.serialization.modules.PolymorphicModuleBuilder

internal actual fun PolymorphicModuleBuilder<Message.Element>.resolvePlatformStandardSerializers() {
    // nothing.
}

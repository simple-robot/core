pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
//    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
}

rootProject.name = "simple-robot"

include(":simbot-common-annotations")
include(":simbot-common-core")
include(":simbot-common-suspend-runner")
include(":simbot-common-stage-loop")

include(":simbot-logger")
include(":simbot-logger-slf4j2-impl")



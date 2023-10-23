rootProject.name = "simple-robot"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include(":simbot-common-annotations")
include(":simbot-common-core")
include(":simbot-common-suspend-runner")
include(":simbot-common-stage-loop")

include(":simbot-logger")
include(":simbot-logger-slf4j2-impl")

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

include(":simbot-commons:simbot-common-annotations")
include(":simbot-commons:simbot-common-core")
include(":simbot-commons:simbot-common-suspend-runner")
include(":simbot-commons:simbot-common-stage-loop")
include(":simbot-api")
include(":simbot-cores:simbot-core")

include(":simbot-logger")
include(":simbot-logger-slf4j2-impl")

include(":simbot-quantcat:simbot-quantcat-annotations")
include(":simbot-quantcat:simbot-quantcat-common")

include(":simbot-cores:simbot-core-spring-boot-starter-common")
include(":simbot-cores:simbot-core-spring-boot-v2-starter")
include(":simbot-cores:simbot-core-spring-boot-starter") // v3

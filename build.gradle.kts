import P.Simbot.group
import P.Simbot.version

plugins {
    idea
//    id("simbot.changelog-generator")
//    id("simbot.nexus-publish")
    id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "4.1.2" apply false

}

group = "love.forte.simbot"
version = "4.0-SNAPSHOT"

//kotlin {
//    jvmToolchain(8)
//}

allprojects {
    repositories {
        mavenCentral()
        love.forte.gradle.common.core.repository.Repositories.Snapshot.Default.apply {
            configMaven {
                mavenContent {
                    snapshotsOnly()
                }
            }
        }
    }
}

//subprojects {
//    plugins.any { it ->
//        println("it: $it")
//        false
//    }
//}

idea {
    module {
        isDownloadSources = true
    }
}

import love.forte.gradle.common.core.project.setup

plugins {
    idea
//    id("simbot.changelog-generator")
//    id("simbot.nexus-publish")
    id("simbot.dokka-multi-module")
    id("com.github.gmazzo.buildconfig") version "4.1.2" apply false
    id("io.gitlab.arturbosch.detekt")
}

group = "love.forte.simbot"
version = "4.0-SNAPSHOT"

// kotlin {
//    jvmToolchain(8)
// }

// https://github.com/detekt/detekt/blob/main/build.gradle.kts

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

    // apply(plugin = "io.gitlab.arturbosch.detekt")
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("io.gitlab.arturbosch.detekt")) {
            return@afterEvaluate
        }

        fun Project.hasKtP(): Boolean {
            return plugins.findPlugin("org.jetbrains.kotlin.jvm") != null ||
                plugins.findPlugin("org.jetbrains.kotlin.multiplatform") != null
        }

        if (hasKtP()) {
//            apply(plugin = "io.gitlab.arturbosch.detekt")
            applyDetekt()
        }
    }
}

fun Project.applyDetekt() {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
//        buildUponDefaultConfig = true
        config.from(rootProject.projectDir.resolve(".detekt/config/detekt.yml"))
        baseline = rootProject.projectDir.resolve(".detekt/baseline/detekt-baseline.xml")
        // "detekt-baseline.xml"
        dependencies {
            detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
        }
    }
}

idea {
    module {
        isDownloadSources = true
    }
}

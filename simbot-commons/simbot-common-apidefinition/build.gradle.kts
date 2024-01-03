/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
//    `simbot-multiplatform-maven-publish`
    `java-library`
    kotlin("multiplatform")
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    modularity.inferModulePath.set(true)
    options.encoding = "UTF-8"
    options.compilerArgumentProviders.add(CommandLineArgumentProvider {
        // Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
        listOf("--patch-module", "simbot.common.apidefinition=${sourceSets["main"].output.asPath}")
    })
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    jvm {
        jvmToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
        withJava()
        compilations.all {
            kotlinOptions {
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-Xjvm-default=all",
                )
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    // tier1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // tier2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // tier3
    // androidNativeArm32()
    // androidNativeArm64()
    // androidNativeX86()
    // androidNativeX64()
    mingwX64()
    // watchosDeviceArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.core)
            api(libs.ktor.http)
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmTest {
            dependencies {
                api(libs.slf4j.api)
            }
        }
    }
}


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
    `java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    applyDefaultHierarchyTemplate()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = JVMConstants.KT_JVM_TARGET
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-Xjvm-default=all",
                    // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. You can use -Xexpect-actual-classes flag to suppress this warning. Also see: https://youtrack.jetbrains.com/issue/KT-61573
                    "-Xexpect-actual-classes"
                )
            }
        }
        withJava()
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
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    // wasm?
//    @Suppress("OPT_IN_USAGE")
//    wasmJs()
//    @Suppress("OPT_IN_USAGE")
//    wasmWasi()

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":simbot-common-annotations"))
                compileOnly(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        nativeMain
        nativeTest

        linuxMain
        linuxTest

        appleMain
        appleTest

    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JVMConstants.KT_JVM_TARGET
    targetCompatibility = JVMConstants.KT_JVM_TARGET
    options.encoding = "UTF-8"
}
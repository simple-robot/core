import love.forte.plugin.suspendtrans.gradle.withKotlinTargets

plugins {
//    `java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
//    id("io.gitlab.arturbosch.detekt")
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

    withKotlinTargets { target ->
        targets.findByName(target.name)?.compilations?.all {
            // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. You can use -Xexpect-actual-classes flag to suppress this warning. Also see: https://youtrack.jetbrains.com/issue/KT-61573
            kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":simbot-common-annotations"))
                api(project(":simbot-common-suspend-runner"))
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

        jvmMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.core)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        jsMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.core)
                implementation(project(":simbot-common-annotations"))
            }
        }

        nativeMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.core)
            }
        }
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

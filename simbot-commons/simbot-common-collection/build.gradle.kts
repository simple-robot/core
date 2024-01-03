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
                this.moduleName

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
                // compileOnly(project(":simbot-commons:simbot-common-annotations"))
                compileOnly(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        jsMain {
            dependencies {
                // implementation(project(":simbot-commons:simbot-common-annotations"))
            }
        }
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JVMConstants.KT_JVM_TARGET
    targetCompatibility = JVMConstants.KT_JVM_TARGET
    options.encoding = "UTF-8"
    modularity.inferModulePath.set(true)
}

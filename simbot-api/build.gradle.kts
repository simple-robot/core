import love.forte.gradle.common.core.project.setup
import love.forte.plugin.suspendtrans.gradle.withKotlinTargets

plugins {
//    `java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("simbot.dokka-module-configuration")
//    id("io.gitlab.arturbosch.detekt")
    id("simbot.suspend-transform-configure")
    alias(libs.plugins.ksp)
}

setup(P.Simbot)

apply(plugin = "simbot-multiplatform-maven-publish")

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
                freeCompilerArgs += "-Xjvm-default=all"
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
                // jvm compile only
                api(libs.jetbrains.annotations)
                api(project(":simbot-commons:simbot-common-annotations"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                api(project(":simbot-commons:simbot-common-collection"))
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
                // suspend reversal annotations
                compileOnly(libs.suspend.reversal.annotations)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.json)
            }
        }



        jvmMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.reactive)
                compileOnly(libs.kotlinx.coroutines.reactor)
                compileOnly(libs.kotlinx.coroutines.rx2)
                compileOnly(libs.kotlinx.coroutines.rx3)

                compileOnly(libs.jetbrains.annotations)
                compileOnly(project(":simbot-commons:simbot-common-annotations"))
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.reactive)
                implementation(libs.kotlinx.coroutines.reactor)
                implementation(libs.kotlinx.coroutines.rx2)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation(libs.ktor.client.core)

                implementation(kotlin("test-junit5"))
                implementation(libs.ktor.client.cio)
            }
        }

        jsMain.dependencies {
            implementation(libs.suspend.reversal.annotations)
        }

        jsTest.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.core)
        }

        nativeTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        linuxTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        appleTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        mingwTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.winhttp)
        }


    }
}

dependencies {
    add("kspJvm", libs.suspend.reversal.processor)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JVMConstants.KT_JVM_TARGET
    targetCompatibility = JVMConstants.KT_JVM_TARGET
    options.encoding = "UTF-8"
}

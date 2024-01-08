plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

configJavaCompileWithModule()

kotlin {
    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)
}

dependencies {
    implementation(project(":simbot-commons:simbot-common-annotation-data-class-annotations"))
    api(libs.ksp)
    api(libs.kotlinPoet.ksp)
    testImplementation(kotlin("test-junit5"))
}

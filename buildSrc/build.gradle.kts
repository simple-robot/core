plugins {
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

val kotlinVersion = "1.9.20"
val dokkaPluginVersion = "1.9.10"
val suspendTransformVersion = "0.5.0"
val gradleCommon = "0.1.1"
val ktor = "2.3.1"

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaPluginVersion")
    implementation("org.jetbrains.dokka:dokka-base:$dokkaPluginVersion")

    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.3.0")
    implementation("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:$suspendTransformVersion")

    implementation("love.forte.gradle.common:gradle-common-core:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-kotlin-multiplatform:$gradleCommon")
    implementation("love.forte.gradle.common:gradle-common-publication:$gradleCommon")

    // ktor
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    // detekt
    // 1.23.1
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.1")
}

idea {
    module {
        isDownloadSources = true
    }
}

/*
java.lang.NoSuchMethodError: 'boolean org.jetbrains.kotlin.backend.common.ir.IrUtilsKt.isStatic(org.jetbrains.kotlin.ir.declarations.IrFunction)'
    at love.forte.plugin.suspendtrans.utils.IrFunctionUtilsKt.paramsAndReceiversAsParamsList(IrFunctionUtils.kt:136)
    at love.forte.plugin.suspendtrans.utils.IrFunctionUtilsKt.createSuspendLambdaWithCoroutineScope(IrFunctionUtils.kt:72)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformerKt.generateTransformBodyForFunction(SuspendTransformTransformer.kt:199)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformerKt.access$generateTransformBodyForFunction(SuspendTransformTransformer.kt:1)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.resolveFunctionBody(SuspendTransformTransformer.kt:172)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.resolveFunctionBodyByDescriptor(SuspendTransformTransformer.kt:84)
    at love.forte.plugin.suspendtrans.ir.SuspendTransformTransformer.visitFunctionNew(SuspendTransformTransformer.kt:68)
 */

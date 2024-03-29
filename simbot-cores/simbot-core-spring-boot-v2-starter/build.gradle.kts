/*
 * Copyright (c) 2021-2023 ForteScarlet.
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
    // id("simbot.boot-module-conventions")
    // `simbot-jvm-maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
}

configJavaCompileWithModule("simbot.core.springboot.common")

kotlin {
    explicitApi()
    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)
}

dependencies {
    compileOnly(project(":simbot-commons:simbot-common-annotations"))
    compileOnly(project(":simbot-quantcat:simbot-quantcat-annotations"))
    api(project(":simbot-quantcat:simbot-quantcat-common"))
    api(project(":simbot-cores:simbot-core"))
    api(project(":simbot-cores:simbot-core-spring-boot-starter-common"))

    compileOnly(libs.spring.boot.v2.logging)

    compileOnly(libs.spring.boot.v2.autoconfigure)
    compileOnly(libs.spring.boot.v2.configuration.processor)
    annotationProcessor(libs.spring.boot.v2.configuration.processor)
    kapt(libs.spring.boot.v2.configuration.processor)

    compileOnly(libs.javax.annotation.api)

    testImplementation(libs.spring.boot.v2.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.properties)
    testImplementation(libs.kotlinx.serialization.protobuf)
    testImplementation(libs.spring.boot.v2.aop)
    testImplementation(libs.spring.boot.v2.autoconfigure)
    testImplementation(libs.spring.boot.v2.configuration.processor)
}


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
//    id("simbot.simple-module-conventions")
//    `simbot-jvm-maven-publish`
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
}

configJavaCompileWithModule("simbot.logger.slf4j2impl")

kotlin {
    explicitApi()
    configJavaToolchain(JVMConstants.KT_JVM_TARGET_VALUE)
}

dependencies {
    api(project(":simbot-logger"))
    api("com.lmax:disruptor:3.4.4")
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }
    packageName.set("love.forte.simbot.logger.slf4j2")
    className.set("SLF4JInformation")
    var slf4jVersion = libs.versions.slf4j.get()
    val last = slf4jVersion.lastIndexOf('.')
    if (last >= 0) {
        slf4jVersion = slf4jVersion.replaceRange(last, slf4jVersion.length, ".99")
    }

    buildConfigField("String", "VERSION", "\"$slf4jVersion\" // auto-generated")

}

/*
 * Copyright (C) 2022 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotest)
    alias(libs.plugins.moko.kswift)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    jvm {
//        compilations.all {
//            kotlinOptions.jvmTarget = "1.8"
//        }
//        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    android()

    val xcf = XCFramework("shared")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "shared"

            xcf.add(this)

            listOf(
                libs.moko.mvvm,
                libs.moko.mvvm.flow
            ).forEach { export(it) }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                api(libs.moko.mvvm)
                api(libs.moko.mvvm.flow)
                implementation(libs.sqlDelight.extensions.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5.jvm)
                implementation(libs.sqlDelight.driver.sqlite)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.sqlDelight.driver.android)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation(libs.sqlDelight.driver.native)
            }

            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
    namespace = "ru.olegivo.repeatodo"
}

kswift {
    install(dev.icerock.moko.kswift.plugin.feature.SealedToSwiftEnumFeature)

    excludeLibrary("kotlinx-coroutines-core")
}

sqldelight {
    database("RepeaTodoDb") {
        packageName = "ru.olegivo.repeatodo.db"
        schemaOutputDirectory = file("build/dbs")
        verifyMigrations = true
    }
}

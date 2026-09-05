/*
 * Copyright (C) 2023 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of RepeaTodo.
 *
 * RepeaTodo is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * RepeaTodo PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * RepeaTodo.
 */

import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SuspendInterop
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.kotlinMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
    alias(libs.plugins.skie)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // AGP 9 KMP Android library plugin: the Android target and its Android
    // configuration live in `kotlin { android { } }`; the old top-level
    // `android {}` + `androidTarget()` combo is gone.
    android {
        namespace = "ru.olegivo.repeatodo"
        compileSdk = 36
        minSdk = 26
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

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

        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.moko.mvvm)
            api(libs.moko.mvvm.flow)
            implementation(libs.sqlDelight.extensions.coroutines)
            implementation(libs.sqlDelight.primitive.adapters)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.driver.sqlite)
        }
        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5.jvm)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.sqlDelight.driver.android)
        }
        iosMain.dependencies {
            implementation(libs.sqlDelight.driver.native)
        }
    }
}

skie {
    features {
        coroutinesInterop.set(false)
        group {
            FlowInterop.Enabled(false)
            SuspendInterop.Enabled(false)
        }
        group("dev.icerock.moko") {
            FlowInterop.Enabled(false)
            SuspendInterop.Enabled(false)
        }
    }
}

sqldelight {
    databases {
        create("RepeaTodoDb") {
            packageName.set("ru.olegivo.repeatodo.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}

// SQLDelight 2 still writes schema *.db next to migration sources. Running
// generateSchema together with generateInterface/verify in one graph trips
// Gradle's implicit dependency check, same as 1.5.5.
listOf(
    "generateCommonMainRepeaTodoDbInterface",
    "verifyCommonMainRepeaTodoDbMigration",
).forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach {
        mustRunAfter("generateCommonMainRepeaTodoDbSchema")
    }
}

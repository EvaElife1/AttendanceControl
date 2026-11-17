plugins {
    alias(libs.plugins.android.application)
    // Если у вас используется Kotlin, добавьте эту строку (обычно она есть по умолчанию):
    // alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.attendancecontrol"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.attendancecontrol"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        // Строка для запуска UI-тестов (Espresso)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Основные библиотеки (у вас уже есть)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // ==========================================
    // БЛОК ДЛЯ АВТОТЕСТОВ (QA / DevOps)
    // ==========================================

    // 1. Unit-тесты (JUnit) - папка (test)
    testImplementation(libs.junit)

    // 2. UI-тесты (Espresso, AndroidX Test) - папка (androidTest)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // ДОБАВИТЬ ВРУЧНУЮ (если их нет в libs.versions.toml):
    // ActivityScenario для управления жизненным циклом Activity в тестах
    androidTestImplementation("androidx.test:core:1.5.0")
    // Rules для тестов (нужен для ActivityScenarioRule)
    androidTestImplementation("androidx.test:rules:1.5.0")
    // Runner для инструментальных тестов
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
}
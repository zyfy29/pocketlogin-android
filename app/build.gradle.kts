plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = 33
    namespace = "com.dx.mobile.captcha.demo"

    defaultConfig {
        applicationId = "com.dx.mobile.captcha.demo_poc"
        minSdk = 21
        targetSdk = 33
        versionCode = 2
        versionName = "0.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // APKファイル名のカスタマイズ
    android.applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "pocketlogin-${versionName}-${buildType.name}.apk"
        }
    }

    signingConfigs {
        getByName("debug") {
            enableV1Signing = true
            enableV2Signing = true
            storeFile = file("demo.p12")
            storePassword = "android"
            keyAlias = "key0"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packagingOptions {
        doNotStrip += "**/lib*Risk*.so"
        doNotStrip += "**/lib*Captcha*.so"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("com.github.jenly1314:zxing-lite:2.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("commons-io:commons-io:2.11.0")
    // FIXME constraintlayout 2.0.4 会导致oppo验证码显示异常, 1.1.2测试正常
    implementation("androidx.constraintlayout:constraintlayout:1.1.2")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.activity:activity:1.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val roomVersion = "2.5.2"

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
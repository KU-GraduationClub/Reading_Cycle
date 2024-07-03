import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

android {
    namespace = "com.example.reading_cycle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reading_cycle"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "api_key", getApiKey("api.key"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
    }


    dependencies {
        // Firebase Storage
        implementation("com.google.firebase:firebase-storage:19.2.2")
        // Glide 추가 (이미지 로드, 표시)
        implementation("com.github.bumptech.glide:glide:4.12.0")

        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        // Map 관련
        implementation("com.google.android.gms:play-services-maps:18.2.0")
        implementation("com.google.android.gms:play-services-location:21.1.0")
        implementation("androidx.fragment:fragment-ktx:1.6.2")

        // Firebase 관련
        implementation("com.google.firebase:firebase-database-ktx:21.0.0")
        implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
        implementation("com.google.firebase:firebase-auth:23.0.0")

        // ImageSlider 라이브러리 추가
        implementation("androidx.viewpager2:viewpager2:1.1.0")


    }
}
dependencies {
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
}

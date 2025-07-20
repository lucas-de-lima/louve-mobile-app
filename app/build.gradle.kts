import java.util.Properties
import java.io.FileInputStream

// Lê as propriedades da keystore para uso no build
val keystorePropertiesFile = rootProject.file("app/keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.lucasdelima.louveapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lucasdelima.louveapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Cria um recurso de string chamado 'web_client_id' com o valor
        // que lemos do nosso arquivo keystore.properties.
        resValue(
            "string",
            "web_client_id",
            keystoreProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")
        )
    }

    // Configuração da assinatura de lançamento
    signingConfigs {
        create("release") {
            // Apenas configure se o arquivo de propriedades existir
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file("louve-app-keystore.jks")
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Diz ao build de release para usar a configuração de assinatura
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Splash Screen API
    implementation(libs.androidx.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coil para imagens da internet
    implementation(libs.coil.compose)

    // Firebase/Firestore
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.google.gms.auth)
    implementation(libs.google.identity.googleid)

    // DataStore
    implementation(libs.datastore.preferences)

    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamicFeaturesFragment)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // JSON serialization library
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
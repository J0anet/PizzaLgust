plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("org.jetbrains.dokka") version "1.9.20"
}

android {
    namespace = "com.joanet.pizzalgustmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.joanet.pizzalgustmobile"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",

        )
        )
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.dokka:android-documentation-plugin:1.9.20")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation ("io.mockk:mockk:1.13.10")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.20")

}
tasks.dokkaHtml.configure {
    outputDirectory.set(file("C:\\Users\\joanet\\Desktop\\DAM\\pizzaLgust\\github\\PizzaLgust\\movil\\documentation"))
}


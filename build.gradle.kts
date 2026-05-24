plugins {
    alias(libs.plugins.android.application) apply false

    // Firebase / Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}
plugins {
    java
}


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()

    maven { url = uri("https://jitpack.io") }

    maven { url = uri("https://plugins.gradle.org/m2/") }

    flatDir { dirs("libs") }
}


dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:23.0.0") //group: 'org.jetbrains', name: 'annotations', version: '23.0.0'
    // https://mvnrepository.com/artifact/org.apache.commons/commons-math3
    implementation("org.apache.commons:commons-math3:3.6.1")



}

object Versions {
    const val junitVersion = "5.8.2"
    // Creates desired junit version that can be called in all subprojects
}
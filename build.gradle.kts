val projectVersion: String by project
val javaSdkVersion: String by project
val bytebuddyVersion: String by project

plugins {
    kotlin("jvm")
}

group = "com.runninglane"
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:$bytebuddyVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaSdkVersion.toInt()))
    }
}
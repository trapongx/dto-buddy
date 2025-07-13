import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val projectVersion: String by project
val javaSdkVersion: String by project
val bytebuddyVersion: String by project
val junitJupiterVersion: String by project
val kotlinPoetVersion: String by project
val javaPoetVersion: String by project
val javaxValidationVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "com.runninglane"
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:$bytebuddyVersion")
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlin("compiler-embeddable"))
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    api("com.squareup:javapoet:$javaPoetVersion")
    api("javax.validation:validation-api:$javaxValidationVersion")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

// Add publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaSdkVersion.toInt()))
    }

    // Configure JSR-305 strict mode for proper nullability handling
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Enable JSR-305 strict mode for proper nullability with Java interop
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }
}
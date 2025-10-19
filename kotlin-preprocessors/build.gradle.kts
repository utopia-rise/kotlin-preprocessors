import versioning.GitTagValueSource
import versioning.GitHashValueSource

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    id("com.utopia-rise.maven-central-publish")
    id("com.gradle.plugin-publish") version "1.2.0"
    `java-gradle-plugin`
}

val baseVersion = "0.2.1"

val exactTag = providers.of(GitTagValueSource::class) {
    parameters.workDir.set(layout.projectDirectory)
}

val shortHash = providers.of(GitHashValueSource::class) {
    parameters.workDir.set(layout.projectDirectory)
    parameters.abbrev.set(7)
}

logger.lifecycle("Current tag is: ${exactTag.orNull}")
logger.lifecycle("Current commit hash is: ${shortHash.orNull}")

val tagValue = exactTag.get()
version = tagValue.ifBlank {
    "$baseVersion-${shortHash.get()}-SNAPSHOT"
}

group = "com.utopia-rise"

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

gradlePlugin {
    website.set("https://github.com/utopia-rise/kotlin-preprocessors")
    plugins {
        create("preProcessorPlugin") {
            id = "com.utopia-rise.kotlin-preprocessors"
            implementationClass = "com.utopiarise.kotlin.preprocessors.gradle.PreProcessorPlugin"
            displayName = "Gradle plugin to define kotlin pre-processors"
            description = "Automatically setup of godot kotlin jvm specific project configurations"

            tags.set(listOf("kotlin", "pre-processors"))
            vcsUrl.set("https://github.com/utopia-rise/kotlin-preprocessors")
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "2.2.0"))
    implementation(kotlin("gradle-plugin-api"))

    implementation("com.squareup:kotlinpoet:1.12.0")

    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.7")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

tasks.test {
    useJUnitPlatform()
}
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    id("org.ajoberstar.grgit") version "4.1.0"
    id("com.utopia-rise.maven-central-publish")
    id("com.gradle.plugin-publish") version "1.1.0"
    `java-gradle-plugin`
}

val baseVersion = "0.1.0"

val grgit = Grgit.open(mapOf("currentDir" to project.rootDir))

val currentCommit: Commit = grgit.head()
// check if the current commit is tagged
var tagOnCurrentCommit = grgit.tag.list().firstOrNull { tag -> tag.commit.id == currentCommit.id }
var releaseMode = tagOnCurrentCommit != null

version = if (!releaseMode) {
    "$baseVersion-${currentCommit.abbreviatedId}-SNAPSHOT"
} else {
    requireNotNull(tagOnCurrentCommit).name
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
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.8.20"))
    implementation("com.squareup:kotlinpoet:1.12.0")

    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.7")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
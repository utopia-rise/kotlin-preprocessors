plugins {
    `kotlin-dsl`
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("mavenCentralPublishPlugin") {
            id = "com.utopia-rise.maven-central-publish"
            displayName = "Gradle plugin for publishing to maven central"
            implementationClass = "publish.mavencentral.PublishToMavenCentralPlugin"
        }
    }
    isAutomatedPublishing = false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "2.0.21"))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
}
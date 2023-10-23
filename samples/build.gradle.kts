plugins {
    kotlin("jvm") version "1.8.20"
    id("com.utopia-rise.kotlin-preprocessors")
    application
}

group = "com.utopia-rise"
version = "0.1.0-SNAPSHOT"

kotlinDefinitions {
    definitionsObjectName.set("SamplesBuildConfig")

    define("DEBUG")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
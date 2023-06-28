
rootProject.name = "samples"

includeBuild("../kotlin-preprocessors") {
    dependencySubstitution {
        substitute(module("com.utopia-rise:kotlin-preprocessors")).using(project(":"))
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "com.utopia-rise.kotlin-preprocessors") {
            useModule("com.utopia-rise:kotlin-preprocessors:${requested.version}")
        }
    }
}
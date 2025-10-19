
rootProject.name = "kotlin-preprocessors"

plugins {
    // downloads missing jdk for `jvmToolchain` see: https://docs.gradle.org/current/userguide/toolchains.html#sub:download_repositories
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0") // https://github.com/gradle/foojay-toolchains/tags
}

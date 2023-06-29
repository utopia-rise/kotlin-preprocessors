package publish.mavencentral

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

class PublishToMavenCentralPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("maven-publish")
        target.plugins.apply("signing")
        target.plugins.apply("org.ajoberstar.grgit")

        target.afterEvaluate {
            val ossrhUser = target.propOrEnv("GODOT_KOTLIN_MAVEN_CENTRAL_TOKEN_USERNAME")
            val ossrhPassword = target.propOrEnv("GODOT_KOTLIN_MAVEN_CENTRAL_TOKEN_PASSWORD")
            val signingKey = target.propOrEnv("GODOT_KOTLIN_GPG_PRIVATE_KEY_ASCII")
            val signingPassword = target.propOrEnv("GODOT_KOTLIN_GPG_KEY_PASSPHRASE")

            val releaseMode = !(target.version as String).endsWith("-SNAPSHOT")

            target.extensions.configure(SigningExtension::class.java) {
                @Suppress("UnstableApiUsage")
                useInMemoryPgpKeys(signingKey, signingPassword)
                target.extensions.findByType(PublishingExtension::class.java)?.publications?.all {
                    sign(this)
                }
            }

            target.extensions.configure(PublishingExtension::class.java) {
                repositories {
                    maven {
                        val targetRepo = if (releaseMode) {
                            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                        } else {
                            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                        }
                        setUrl(targetRepo)

                        credentials {
                            username = ossrhUser
                            password = ossrhPassword
                        }
                    }
                }
                publications {
                    all {
                        if (this is MavenPublication) {
                            pom {
                                name.set("kotlin-preprocessors")
                                description.set("Gradle plugin to define preprocessors for kotlin language")

                                url.set("https://github.com/utopia-rise/kotlin-preprocessors.git")

                                scm {
                                    connection.set("scm:git:https://github.com/utopia-rise/kotlin-preprocessors")
                                    developerConnection.set("scm:git:github.com:utopia-rise/kotlin-preprocessors.git")
                                    tag.set("master") //FIXME
                                    url.set("https://github.com/utopia-rise/kotlin-preprocessors")
                                }

                                licenses {
                                    license {
                                        name.set("MIT License")
                                        url.set("https://github.com/utopia-rise/kotlin-preprocessors/blob/master/LICENSE")
                                        distribution.set("repo")
                                    }
                                }

                                developers {
                                    developer {
                                        id.set("core")
                                        name.set("Pierre-Thomas Meisels")
                                        url.set("https://github.com/piiertho")
                                        email.set("meisels27@yahoo.fr")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Project.propOrEnv(name: String): String? {
    var property: String? = findProperty(name) as String?
    if (property == null) {
        property = System.getenv(name)
    }
    return property
}
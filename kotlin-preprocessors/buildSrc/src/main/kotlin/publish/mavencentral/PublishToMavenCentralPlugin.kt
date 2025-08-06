package publish.mavencentral

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.Sign

@Suppress("unused") // false positive
class PublishToMavenCentralPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(org.gradle.api.publish.maven.plugins.MavenPublishPlugin::class.java)

        target.afterEvaluate {
            val mavenCentralUser = target.propOrEnv("ORG_GRADLE_PROJECT_mavenCentralUsername") ?: target.propOrEnv("mavenCentralUsername")
            val mavenCentralPassword = target.propOrEnv("ORG_GRADLE_PROJECT_mavenCentralPassword") ?: target.propOrEnv("mavenCentralPassword")
            val gpgInMemoryKey = target.propOrEnv("ORG_GRADLE_PROJECT_signingInMemoryKey") ?: target.propOrEnv("signingInMemoryKey")
            val gpgPassword = target.propOrEnv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword") ?: target.propOrEnv("signingInMemoryKeyPassword")

            val canSign = mavenCentralUser != null && mavenCentralPassword != null && gpgInMemoryKey != null && gpgPassword != null

            target.extensions.getByType(PublishingExtension::class.java).apply {
                publications {
                    all {
                        if (this is MavenPublication) {
                            groupId = "com.utopia-rise"
                            artifactId = if (artifactId.isNullOrEmpty()) target.name else artifactId
                            version = target.version as String

                            pom {
                                url.set("https://github.com/utopia-rise/kotlin-preprocessors.git")

                                if (name.getOrElse("").isNullOrEmpty()) {
                                    name.set(target.name)
                                }
                                if (description.getOrElse("").isNullOrEmpty()) {
                                    description.set(target.description ?: "Godot kotlin jvm module")
                                }

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
                                        name.set("Ranie Jade Ramiso")
                                        url.set("https://github.com/raniejade")
                                        email.set("raniejaderamiso@gmail.com")
                                    }
                                    developer {
                                        id.set("core")
                                        name.set("Pierre-Thomas Meisels")
                                        url.set("https://github.com/piiertho")
                                        email.set("meisels27@yahoo.fr")
                                    }
                                    developer {
                                        id.set("core")
                                        name.set("Cedric Hippmann")
                                        url.set("https://github.com/chippmann")
                                        email.set("cedric.hippmann@hotmail.com")
                                    }
                                    developer {
                                        id.set("core")
                                        name.set("Tristan Grespinet")
                                        url.set("https://github.com/CedNaru")
                                        email.set("ced.naru@gmail.com")
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (canSign) {
                logger.info("Will sign artifact for project \"${name}\" and setup publishing")

                pluginManager.apply(MavenPublishPlugin::class.java)
                extensions.getByType(MavenPublishBaseExtension::class.java).apply {
                    publishToMavenCentral()
                    signAllPublications()
                }

                target.afterEvaluate {
                    target
                        .tasks
                        .filter { task -> task.name.startsWith("publish") }
                        .forEach { task ->
                            task.dependsOn(target.tasks.withType(Sign::class.java))
                        }
                }
            } else {
                logger.warn("Cannot sign project \"${name}\" as credentials are missing. Will not setup signing and remote publishing credentials. Publishing will only work to maven local!")
            }
        }
    }
}

fun Project.propOrEnv(name: String): String? {
    return findProperty(name) as? String?
        ?: System.getenv(name)?.ifEmpty { null }
        ?: providers.systemProperty(name).orNull
}
package com.utopiarise.kotlin.preprocessors.gradle

import com.utopiarise.kotlin.preprocessors.codegen.generateDefinitions
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

open class PreProcessorPluginExtension(objects: ObjectFactory) {
    internal val definitions = mutableMapOf<String, Any>()
    val definitionsObjectPrefix = objects.property(String::class.java)

    fun define(definition: String) {
        definitions[definition] = true
    }

    fun define(definition: String, value: Any) {
        val valueClass = value::class
        requireNotNull(valueClass.javaPrimitiveType) {
            buildString {
                appendLine("Cannot create definition $definition with type $valueClass.")
                appendLine("Definitions can only be primitives or strings.")
            }
        }

        definitions[definition] = value
    }
}

open class GenerateDefinitions : DefaultTask() {
    @Input
    val definitions = project.objects.mapProperty(String::class.java, Any::class.java)

    @Input
    val definitionsObjectPrefix = project.objects.property(String::class.java)

    @TaskAction
    fun execute() {
        val output = project.buildDir.resolve("definitions")
        output.deleteRecursively()

        output.generateDefinitions(definitions.get(), definitionsObjectPrefix.get())
    }
}

class PreProcessorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("kotlinDefinitions", PreProcessorPluginExtension::class.java)
        val generationTask = project.tasks.register("generateKotlinDefinitions", GenerateDefinitions::class.java) {
            definitions.set(extension.definitions)
            definitionsObjectPrefix.set(extension.definitionsObjectPrefix)

            group = "kotlin-definitions"
            description = "Generate definitions for kotlin as constants."
        }

        project.pluginManager.apply("org.jetbrains.gradle.plugin.idea-ext")

        project.rootProject.ideaExtension.project.settings {
            taskTriggers {
                afterSync(generationTask)
            }
        }

        project
            .kotlinExtension
            .sourceSets
            .getByName("main")
            .kotlin
            .srcDirs(project.buildDir.resolve("definitions"))
        project.getTasksByName("compileKotlin", false).first().dependsOn(generationTask)
    }
}

val Project.ideaExtension: IdeaModel
    get() = requireNotNull(
        extensions
            .findByType(IdeaModel::class.java)
    ) {
        "idea extension not found"
    }
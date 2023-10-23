package com.utopiarise.kotlin.preprocessors.gradle

import com.utopiarise.kotlin.preprocessors.codegen.generateDefinitions
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

private const val DEFAULT_DEFINITION_CLASS = "BuildConfig"

open class PreProcessorPluginExtension(objects: ObjectFactory) {
    internal val definitions = mutableMapOf<String, Any>()
    val definitionsObjectName = objects.property(String::class.java)

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
    val definitionsObjectName = project.objects.property(String::class.java)

    @TaskAction
    fun execute() {
        val output = project.buildDir.resolve("definitions")
        output.deleteRecursively()

        output.generateDefinitions(definitions.get(), definitionsObjectName.getOrElse(DEFAULT_DEFINITION_CLASS))
    }
}

class PreProcessorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.rootProject.pluginManager.apply(IdeaPlugin::class.java)
        project.rootProject.pluginManager.apply(IdeaExtPlugin::class.java)

        if (project != project.rootProject) {
            project.pluginManager.apply(IdeaPlugin::class.java)
            project.pluginManager.apply(IdeaExtPlugin::class.java)
        }

        val extension = project.extensions.create("kotlinDefinitions", PreProcessorPluginExtension::class.java)
        val generationTask = project.tasks.register("generateKotlinDefinitions", GenerateDefinitions::class.java) {
            definitions.set(extension.definitions)
            definitionsObjectName.set(extension.definitionsObjectName)

            group = "kotlin-definitions"
            description = "Generate definitions for kotlin as constants."
        }

        val generatedKotlinDefinitionsDirectory = project.buildDir.resolve("definitions")

        project
            .kotlinExtension
            .sourceSets
            .getByName("main")
            .kotlin
            .srcDirs(generatedKotlinDefinitionsDirectory)

        project.getTasksByName("compileKotlin", false).first().dependsOn(generationTask)

        project.rootProject.ideaExtension.project.settings {
            taskTriggers {
                afterSync(generationTask)
            }
        }

        project.ideaExtension.apply {
            module {
                generatedSourceDirs.add(generatedKotlinDefinitionsDirectory)
            }
        }
    }
}

val Project.ideaExtension: IdeaModel
    get() = requireNotNull(
        extensions
            .findByType(IdeaModel::class.java)
    ) {
        "idea extension not found"
    }
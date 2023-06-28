package com.utopiarise.kotlin.preprocessors.codegen

import com.squareup.kotlinpoet.*
import java.io.File

fun File.generateDefinitions(definitions: Map<String, Any>, definitionsObjectPrefix: String) {
    val definitionsContainerName = "${definitionsObjectPrefix}Definitions"

    val definitionsFile = FileSpec
        .builder(definitionsPackage, definitionsContainerName)

    val definitionsObject = TypeSpec
        .objectBuilder(definitionsContainerName)

    for (definition in definitions) {
        val definitionValue = definition.value
        val definitionType = definitionValue::class
        val definitionName = definition.key
        definitionsObject.addProperty(
            PropertySpec
                .builder(definitionName, definitionType)
                .initializer("%L", definitionValue)
                .addModifiers(KModifier.CONST)
                .build()
        )
    }

    definitionsFile.addType(definitionsObject.build())
    definitionsFile.build().writeTo(this)
}
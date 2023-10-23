package com.utopiarise.kotlin.preprocessors.codegen

import com.squareup.kotlinpoet.*
import java.io.File

fun File.generateDefinitions(definitions: Map<String, Any>, definitionsObjectName: String) {
    val definitionsFile = FileSpec
        .builder(definitionsPackage, definitionsObjectName)

    val definitionsObject = TypeSpec
        .objectBuilder(definitionsObjectName)

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
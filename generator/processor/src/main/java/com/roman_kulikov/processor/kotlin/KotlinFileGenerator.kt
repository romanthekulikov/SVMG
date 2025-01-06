package com.roman_kulikov.processor.kotlin

import com.roman_kulikov.processor.FILE_NAME_ADDITIONAL
import com.roman_kulikov.processor.FlowClass
import com.roman_kulikov.processor.FlowFunctions
import com.roman_kulikov.processor.ViewModelClass
import com.roman_kulikov.processor.generators.FileGenerator
import com.roman_kulikov.processor.models.StateModel
import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import javax.annotation.processing.Generated

internal class KotlinFileGenerator(private val codeGenerator: CodeGenerator) : FileGenerator {
    override fun generateStateVM(stateModel: StateModel) {
        println("Writing file: ${stateModel.name + FILE_NAME_ADDITIONAL}")
        stateModel.toFileSpec().writeTo(codeGenerator, aggregating = false)
    }

    private fun StateModel.toFileSpec(): FileSpec {
        val handlerType = TypeSpec.classBuilder(name + FILE_NAME_ADDITIONAL)
            .superclass(ViewModelClass)
            .addModifiers(KModifier.OPEN)
        properties.forEach { handlerType.addProperty(it) }
        functions.forEach { handlerType.addFunction(it) }

        return FileSpec.builder(fileName = name + FILE_NAME_ADDITIONAL, packageName = packageName)
            .addImport(FlowClass, names = FlowFunctions)
            .addType(handlerType.build())
            .build()
    }
}
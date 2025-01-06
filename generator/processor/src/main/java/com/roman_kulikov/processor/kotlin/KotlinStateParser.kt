package com.roman_kulikov.processor.kotlin

import com.roman_kulikov.processor.annotations.FunctionTarget
import com.roman_kulikov.processor.annotations.StateField
import com.roman_kulikov.processor.EXCEPTION_REQUIRED_DATA_CLASS
import com.roman_kulikov.processor.MutableStateFlowClass
import com.roman_kulikov.processor.PROTECTED_STATE
import com.roman_kulikov.processor.PROTECTED_STATE_INITIALIZER_FORMAT
import com.roman_kulikov.processor.STATE
import com.roman_kulikov.processor.STATE_INITIALIZER
import com.roman_kulikov.processor.StateFlowClass
import com.roman_kulikov.processor.models.StateModel
import com.roman_kulikov.processor.parser.StateFunBuilder
import com.roman_kulikov.processor.parser.StateParser
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toClassName

internal class KotlinStateParser(val logger: KSPLogger) : StateParser {
    private val funBuilder: StateFunBuilder = KotlinStateFunBuilder()

    @OptIn(KspExperimental::class)
    override fun parse(classDeclaration: KSClassDeclaration): StateModel {
        logger.info("Start state parser parse")
        if (!classDeclaration.modifiers.contains(Modifier.DATA)) {
            logger.info("Find error: state is not data class")
            throw IllegalStateException(EXCEPTION_REQUIRED_DATA_CLASS)
        }

        val functionList = mutableListOf<FunSpec>()
        val properties = classDeclaration.getDeclaredProperties()
        logger.info("Find properties: $properties")
        val stateFieldAnnotatedProperty = properties.filter {
            it.annotations.find { annotation ->
                annotation.shortName.asString() == StateField::class.simpleName
            } != null
        }
        logger.info("Find annotated properties: $stateFieldAnnotatedProperty")

        stateFieldAnnotatedProperty.forEach {
            val stateFieldTargets = it.getAnnotationsByType(StateField::class).last().target
            stateFieldTargets.forEach { target ->
                when (target) {
                    FunctionTarget.SET -> functionList.add(funBuilder.buildSet(it))
                    FunctionTarget.DELETE -> functionList.add(funBuilder.buildDelete(it))
                    FunctionTarget.UPDATE -> functionList.add(funBuilder.buildUpdate(it))
                    else -> {}
                }
            }
        }
        val state = StateModel(
            packageName = classDeclaration.packageName.asString(),
            name = classDeclaration.toClassName().simpleName,
            functions = functionList,
            properties = getStateProperties(classDeclaration)
        )
        logger.info("Parsed StateModel: $state")

        return state
    }

    private fun getStateProperties(classDeclaration: KSClassDeclaration): List<PropertySpec> {
        val stateClassName = classDeclaration.simpleName.asString()
        val properties = mutableListOf<PropertySpec>()
        properties.add(
            PropertySpec.builder(
                name = PROTECTED_STATE,
                type = MutableStateFlowClass
                    .parameterizedBy(ClassName(classDeclaration.packageName.asString(), stateClassName)),
                modifiers = listOf(KModifier.PROTECTED)
            )
                .initializer(
                    PROTECTED_STATE_INITIALIZER_FORMAT,
                    MutableStateFlowClass,
                    ClassName(classDeclaration.packageName.asString(), stateClassName)
                )
                .build()
        )
        properties.add(
            PropertySpec.builder(
                name = STATE,
                type = StateFlowClass
                    .parameterizedBy(ClassName(classDeclaration.packageName.asString(), stateClassName)),
                modifiers = listOf(KModifier.PUBLIC)
            ).initializer(STATE_INITIALIZER)
                .build()
        )

        return properties
    }
}
package com.roman_kulikov.processor.kotlin

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.roman_kulikov.processor.AutoCloseableClass
import com.roman_kulikov.processor.EXCEPTION_REQUIRED_DATA_CLASS
import com.roman_kulikov.processor.MutableStateFlowClass
import com.roman_kulikov.processor.PROTECTED_STATE
import com.roman_kulikov.processor.PROTECTED_STATE_INITIALIZER_FORMAT
import com.roman_kulikov.processor.STATE
import com.roman_kulikov.processor.STATE_INITIALIZER
import com.roman_kulikov.processor.StateFlowClass
import com.roman_kulikov.processor.annotations.FunctionTarget
import com.roman_kulikov.processor.annotations.StateField
import com.roman_kulikov.processor.models.StateModel
import com.roman_kulikov.processor.parser.StateFunBuilder
import com.roman_kulikov.processor.parser.StateParser
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toClassName

internal class KotlinStateParser(val logger: KSPLogger) : StateParser {
    private val funBuilder: StateFunBuilder = KotlinStateFunBuilder()

    @OptIn(KspExperimental::class)
    override fun parse(classDeclaration: KSClassDeclaration): StateModel {
        if (!classDeclaration.modifiers.contains(Modifier.DATA)) {
            throw IllegalStateException(EXCEPTION_REQUIRED_DATA_CLASS)
        }

        val properties = classDeclaration.getDeclaredProperties()
        val stateFieldAnnotatedProperty = properties.filter {
            it.annotations.find { annotation ->
                annotation.shortName.asString() == StateField::class.simpleName
            } != null
        }
        val functionList = getFunctionList(stateFieldAnnotatedProperty)

        val state = StateModel(
            packageName = classDeclaration.packageName.asString(),
            name = classDeclaration.toClassName().simpleName,
            functions = functionList,
            properties = getStateProperties(classDeclaration),
            constructors = getConstructorList()
        )

        return state
    }

    @OptIn(KspExperimental::class)
    private fun getFunctionList(property: Sequence<KSPropertyDeclaration>): List<FunSpec> {
        return buildList {
            property.forEach {
                val stateFieldTargets = it.getAnnotationsByType(StateField::class).last().target
                stateFieldTargets.forEach { target ->
                    when (target) {
                        FunctionTarget.SET -> add(funBuilder.buildSet(it))
                        FunctionTarget.DELETE -> add(funBuilder.buildDelete(it))
                        FunctionTarget.UPDATE -> add(funBuilder.buildUpdate(it))
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getConstructorList(): List<FunSpec> {
        val baseConstructorFunc = FunSpec.constructorBuilder()
            .callSuperConstructor()
            .build()

        val closeablesParam = ParameterSpec.builder("closeables", AutoCloseableClass)
            .addModifiers(KModifier.VARARG)
            .build()
        val closeablesConstructor = FunSpec.constructorBuilder()
            .addParameter(closeablesParam)
            .callSuperConstructor("*${closeablesParam.name}")
            .build()

        return listOf(baseConstructorFunc, closeablesConstructor)
    }

    private fun getStateProperties(classDeclaration: KSClassDeclaration): List<PropertySpec> {
        val stateClassName = classDeclaration.simpleName.asString()
        val properties = mutableListOf<PropertySpec>()
        properties.add(
            PropertySpec.builder(
                name = PROTECTED_STATE,
                type = MutableStateFlowClass
                    .parameterizedBy(
                        ClassName(
                            classDeclaration.packageName.asString(),
                            stateClassName
                        )
                    ),
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
                    .parameterizedBy(
                        ClassName(
                            classDeclaration.packageName.asString(),
                            stateClassName
                        )
                    ),
                modifiers = listOf(KModifier.PUBLIC)
            ).initializer(STATE_INITIALIZER)
                .build()
        )

        return properties
    }
}
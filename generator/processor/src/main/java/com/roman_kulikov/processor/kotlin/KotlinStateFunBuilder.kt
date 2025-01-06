package com.roman_kulikov.processor.kotlin

import com.roman_kulikov.processor.BooleanClass
import com.roman_kulikov.processor.DELETE
import com.roman_kulikov.processor.EXCEPTION_REQUIRED_BOOLEAN_TYPE
import com.roman_kulikov.processor.EXCEPTION_REQUIRED_NULLABLE_TYPE
import com.roman_kulikov.processor.FUNCTION_DELETE_FORMAT
import com.roman_kulikov.processor.FUNCTION_SET_FORMAT
import com.roman_kulikov.processor.FUNCTION_NULL_UPDATE_FORMAT
import com.roman_kulikov.processor.FUNCTION_UPDATE_FORMAT
import com.roman_kulikov.processor.SET
import com.roman_kulikov.processor.UPDATE
import com.roman_kulikov.processor.VALUE
import com.roman_kulikov.processor.parser.StateFunBuilder
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal class KotlinStateFunBuilder : StateFunBuilder {
    override fun buildSet(property: KSPropertyDeclaration) = FunSpec.builder(SET + property.nameFirstUppercaseChar())
        .addParameter(
            ParameterSpec(
                name = VALUE,
                property.type.toTypeName()
            )
        )
        .addModifiers(KModifier.OPEN)
        .addCode(CodeBlock.of(FUNCTION_SET_FORMAT, property.simpleName.asString()))
        .build()

    override fun buildUpdate(property: KSPropertyDeclaration): FunSpec {
        if (property.type.resolve().toClassName() != BooleanClass) {
            throw IllegalArgumentException(property.type.toTypeName().toString() + EXCEPTION_REQUIRED_BOOLEAN_TYPE)
        }
        return if (property.type.toTypeName().isNullable) {
            FunSpec.builder(UPDATE + property.nameFirstUppercaseChar())
                .addCode(CodeBlock.of(FUNCTION_NULL_UPDATE_FORMAT, property.simpleName.asString(), property.simpleName.asString()))
                .addModifiers(KModifier.OPEN)
                .build()
        } else {
            FunSpec.builder(UPDATE + property.nameFirstUppercaseChar())
                .addCode(CodeBlock.of(FUNCTION_UPDATE_FORMAT, property.simpleName.asString(), property.simpleName.asString()))
                .addModifiers(KModifier.OPEN)
                .build()
        }
    }

    override fun buildDelete(property: KSPropertyDeclaration): FunSpec {
        if (!property.type.toTypeName().isNullable) {
            throw IllegalArgumentException(property.simpleName.asString() + EXCEPTION_REQUIRED_NULLABLE_TYPE)
        }

        return FunSpec.builder(DELETE + property.nameFirstUppercaseChar())
            .addCode(FUNCTION_DELETE_FORMAT, property.simpleName.asString())
            .addModifiers(KModifier.OPEN)
            .build()
    }

    private fun KSPropertyDeclaration.nameFirstUppercaseChar(): String {
        return this.simpleName.asString().replaceFirstChar { it.uppercaseChar() }
    }
}
package com.roman_kulikov.processor.parser

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FunSpec

internal interface StateFunBuilder {
    fun buildSet(property: KSPropertyDeclaration): FunSpec
    fun buildUpdate(property: KSPropertyDeclaration): FunSpec
    fun buildDelete(property: KSPropertyDeclaration): FunSpec
}
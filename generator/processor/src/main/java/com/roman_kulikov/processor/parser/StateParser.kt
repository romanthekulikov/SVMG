package com.roman_kulikov.processor.parser

import com.roman_kulikov.processor.models.StateModel
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal interface StateParser {
    fun parse(classDeclaration: KSClassDeclaration): StateModel
}
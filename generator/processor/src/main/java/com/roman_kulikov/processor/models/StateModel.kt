package com.roman_kulikov.processor.models

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec

internal data class StateModel(
    val packageName: String,
    val name: String,
    val properties: List<PropertySpec>,
    val functions: List<FunSpec>,
    val constructors: List<FunSpec>
)

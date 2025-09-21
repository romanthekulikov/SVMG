package com.roman_kulikov.processor.generators

import com.google.devtools.ksp.symbol.KSFile
import com.roman_kulikov.processor.models.StateModel

internal interface FileGenerator {
    fun generateStateVM(stateModel: StateModel, ksFile: KSFile?)
}
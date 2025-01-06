package com.roman_kulikov.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.roman_kulikov.processor.UIStateGenerateProcessor

class UIStateGenerateProcessProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return UIStateGenerateProcessor(environment.codeGenerator, environment.logger)
    }
}
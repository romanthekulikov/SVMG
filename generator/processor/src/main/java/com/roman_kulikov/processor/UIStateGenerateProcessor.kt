package com.roman_kulikov.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.roman_kulikov.processor.annotations.UIState
import kotlin.reflect.KClass

class UIStateGenerateProcessor(private val codeGenerator: CodeGenerator, val logger: KSPLogger) :
    SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val visitor = UIStateGenerateVisitor(codeGenerator, logger)
        val symbols = resolver.getSymbols(UIState::class)
        val validatedSymbols = symbols.filter { it.validate() }.toList()
        logger.warn("${symbols.count()} symbols ${validatedSymbols.count()} validatedSymbols")
        logger.warn("${UIState::class.qualifiedName}")
        validatedSymbols.forEach { symbol ->
            symbol.accept(visitor, Unit)
        }

        return validatedSymbols.toList() - symbols.toSet()
    }

    private fun Resolver.getSymbols(cls: KClass<*>) =
        this.getSymbolsWithAnnotation(cls.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
}
package com.roman_kulikov.processor

import com.roman_kulikov.processor.kotlin.KotlinFileGenerator
import com.roman_kulikov.processor.kotlin.KotlinStateParser
import com.roman_kulikov.processor.parser.StateParser
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class UIStateGenerateVisitor(private val codeGenerator: CodeGenerator, val logger: KSPLogger) : KSVisitorVoid() {
    private val stateParser: StateParser = KotlinStateParser(logger)

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        logger.info("Start create state model")
        val stateModel = stateParser.parse(classDeclaration)
        logger.info("state model created")
        KotlinFileGenerator(codeGenerator).generateStateVM(stateModel)
    }
}
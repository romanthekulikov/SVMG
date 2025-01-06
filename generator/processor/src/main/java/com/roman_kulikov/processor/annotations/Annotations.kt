package com.roman_kulikov.processor.annotations

/**
 * This annotation for class with UI State
 */

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class UIState

/**
 * This annotation specifies what functions to create for the field.
 *
 * @param target: array of [FunctionTarget], available target are:
 * @property [FunctionTarget.SET] -> Create function setField(value: PropertyType)
 * @property [FunctionTarget.UPDATE] -> Create function updateField() to switch property, required PropertyType is Boolean
 * @property [FunctionTarget.DELETE] -> Create deleteField() function to set property to null, nullable PropertyType required
 * @property [FunctionTarget.IGNORE] -> Default value to ignore field for annotation processor
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class StateField(vararg val target: FunctionTarget = [FunctionTarget.IGNORE])
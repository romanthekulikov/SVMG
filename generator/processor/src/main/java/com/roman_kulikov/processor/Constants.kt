package com.roman_kulikov.processor

import com.squareup.kotlinpoet.ClassName

val ViewModelClass = ClassName("androidx.lifecycle", simpleNames = listOf("ViewModel"))
val FlowClass = ClassName("kotlinx.coroutines", listOf("flow"))
val MutableStateFlowClass = ClassName("kotlinx.coroutines.flow", simpleNames = listOf("MutableStateFlow"))
val StateFlowClass = ClassName("kotlinx.coroutines.flow", simpleNames = listOf("StateFlow"))
val BooleanClass = ClassName("kotlin", listOf("Boolean"))

val FlowFunctions = listOf("asStateFlow", "update")

const val EXCEPTION_REQUIRED_NULLABLE_TYPE = " field: Deletable property required nullable type"
const val EXCEPTION_REQUIRED_BOOLEAN_TYPE = " field: Updatable property required Boolean type\""
const val EXCEPTION_REQUIRED_DATA_CLASS = "@UIState can annotate a data class only"

const val FILE_NAME_ADDITIONAL = "Handler"
const val PROTECTED_STATE = "_state"
const val STATE = "state"
const val PROTECTED_STATE_INITIALIZER_FORMAT = "%T(%T())"
const val FUNCTION_SET_FORMAT = "_state.update { it.copy(%N = value) }"
const val FUNCTION_NULL_UPDATE_FORMAT =
    "val updatableValue = _state.value.%N\nif (updatableValue != null) {\n_state.update { it.copy(%N = !updatableValue)\n}\n}"
const val FUNCTION_UPDATE_FORMAT = "_state.update { it.copy(%N = !_state.value.%N) }"
const val FUNCTION_DELETE_FORMAT = "_state.update { it.copy(%N = null) }"
const val STATE_INITIALIZER = "_state.asStateFlow()"
const val VALUE = "value"
const val SET = "set"
const val UPDATE = "update"
const val DELETE = "delete"
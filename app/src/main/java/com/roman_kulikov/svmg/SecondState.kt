package com.roman_kulikov.svmg

import com.roman_kulikov.processor.annotations.FunctionTarget
import com.roman_kulikov.processor.annotations.StateField
import com.roman_kulikov.processor.annotations.UIState

@UIState
data class SecondState(
    @StateField(FunctionTarget.SET)
    val s: String = ""
)
package com.roman_kulikov.svmg

import com.roman_kulikov.processor.annotations.FunctionTarget
import com.roman_kulikov.processor.annotations.StateField
import com.roman_kulikov.processor.annotations.UIState

@UIState
class MainState(
    @StateField(FunctionTarget.SET)
    val humanParam: HumanParam = HumanParam("", 0)
)

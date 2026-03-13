package com.luisnavarro.fevertest.core.testing

object TestRuntime {
    const val UiTestModeProperty: String = "fevertest.uiTestMode"

    val isUiTestMode: Boolean
        get() = System.getProperty(UiTestModeProperty) == "true"
}

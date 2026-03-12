package com.luisnavarro.fevertest.feature.weather

sealed interface WeatherUiAction {
    data object RefreshClicked : WeatherUiAction
    data object RetryClicked : WeatherUiAction
}

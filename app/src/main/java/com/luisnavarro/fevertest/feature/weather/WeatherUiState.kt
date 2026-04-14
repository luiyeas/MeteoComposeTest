package com.luisnavarro.fevertest.feature.weather

sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Error(
        val message: String,
    ) : WeatherUiState

    data class Content(
        val weather: WeatherUiModel,
        val isRefreshing: Boolean = false,
        val recoverableErrorMessage: String? = null,
    ) : WeatherUiState
}

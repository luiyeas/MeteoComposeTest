package com.luisnavarro.fevertest.feature.weather

data class WeatherUiState(
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weather: WeatherUiModel? = null,
    val errorMessage: String? = null,
) {
    val showBlockingError: Boolean
        get() = weather == null && !errorMessage.isNullOrBlank() && !isInitialLoading
}

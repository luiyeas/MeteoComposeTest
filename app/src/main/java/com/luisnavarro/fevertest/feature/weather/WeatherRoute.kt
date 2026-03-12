package com.luisnavarro.fevertest.feature.weather

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WeatherRoute(
    factory: WeatherViewModelFactory,
    modifier: Modifier = Modifier,
) {
    val viewModel: WeatherViewModel = viewModel(factory = factory)
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    WeatherScreen(
        uiState = uiState.value,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

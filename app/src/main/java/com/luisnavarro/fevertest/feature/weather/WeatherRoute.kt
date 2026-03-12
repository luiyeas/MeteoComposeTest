package com.luisnavarro.fevertest.feature.weather

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WeatherRoute(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    WeatherScreen(
        uiState = uiState.value,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

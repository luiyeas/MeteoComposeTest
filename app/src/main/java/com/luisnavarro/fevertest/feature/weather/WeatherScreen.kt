package com.luisnavarro.fevertest.feature.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object WeatherScreenTestTags {
    const val BlockingError = "blocking_error"
    const val InlineErrorBanner = "inline_error_banner"
    const val LoadingState = "loading_state"
    const val LocationCard = "location_card"
    const val LocationMapPlaceholder = "location_map_placeholder"
    const val LocationTitle = "location_title"
    const val RefreshFab = "refresh_fab"
    const val WeatherContent = "weather_content"
}

@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    onAction: (WeatherUiAction) -> Unit,
    showLocationMap: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val isFabLoading = when (uiState) {
        WeatherUiState.Loading -> true
        is WeatherUiState.Content -> uiState.isRefreshing
        is WeatherUiState.Error -> false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            RefreshActionButton(
                isLoading = isFabLoading,
                onClick = {
                    when (uiState) {
                        WeatherUiState.Loading -> Unit
                        is WeatherUiState.Content -> onAction(WeatherUiAction.RefreshClicked)
                        is WeatherUiState.Error -> onAction(WeatherUiAction.RetryClicked)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            WeatherTopBar()

            when (uiState) {
                WeatherUiState.Loading -> LoadingContent(
                    modifier = Modifier.weight(1f),
                )

                is WeatherUiState.Error -> BlockingErrorContent(
                    message = uiState.message,
                    onRetry = { onAction(WeatherUiAction.RetryClicked) },
                    modifier = Modifier.weight(1f),
                )

                is WeatherUiState.Content -> WeatherContent(
                    weather = uiState.weather,
                    isRefreshing = uiState.isRefreshing,
                    errorMessage = uiState.recoverableErrorMessage,
                    showLocationMap = showLocationMap,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

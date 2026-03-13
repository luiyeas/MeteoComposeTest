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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            RefreshActionButton(
                isLoading = uiState.isInitialLoading || uiState.isRefreshing,
                onClick = {
                    onAction(
                        if (uiState.weather == null) {
                            WeatherUiAction.RetryClicked
                        } else {
                            WeatherUiAction.RefreshClicked
                        }
                    )
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

            when {
                uiState.isInitialLoading && uiState.weather == null -> LoadingContent(
                    modifier = Modifier.weight(1f),
                )

                uiState.showBlockingError -> BlockingErrorContent(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = { onAction(WeatherUiAction.RetryClicked) },
                    modifier = Modifier.weight(1f),
                )

                uiState.weather != null -> WeatherContent(
                    weather = uiState.weather,
                    isRefreshing = uiState.isRefreshing,
                    errorMessage = uiState.errorMessage,
                    showLocationMap = showLocationMap,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

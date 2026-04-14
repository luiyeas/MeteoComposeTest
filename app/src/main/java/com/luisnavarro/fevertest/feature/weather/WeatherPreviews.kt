package com.luisnavarro.fevertest.feature.weather

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.ui.theme.FeverTestTheme

@Preview(name = "Weather Screen - Light", showBackground = true)
@Composable
private fun WeatherScreenLightPreview() {
    FeverTestTheme(darkTheme = false) {
        WeatherScreen(
            uiState = WeatherUiState.Content(weather = previewWeatherUiModel()),
            onAction = {},
        )
    }
}

@Preview(name = "Weather Screen - Dark", showBackground = true, backgroundColor = 0xFF101418)
@Composable
private fun WeatherScreenDarkPreview() {
    FeverTestTheme(darkTheme = true) {
        WeatherScreen(
            uiState = WeatherUiState.Content(weather = previewWeatherUiModel()),
            onAction = {},
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun LoadingContentPreview() {
    PreviewCardSurface {
        LoadingContent()
    }
}

@Preview(name = "Blocking Error", showBackground = true)
@Composable
private fun BlockingErrorContentPreview() {
    PreviewCardSurface {
        BlockingErrorContent(
            message = "We couldn't reach the weather service. Please try again.",
            onRetry = {},
        )
    }
}

@Preview(name = "Summary Card", showBackground = true)
@Composable
private fun SummaryCardPreview() {
    PreviewCardSurface {
        SummaryCard(weather = previewWeatherUiModel())
    }
}

@Preview(name = "Stat Card", showBackground = true)
@Composable
private fun StatCardPreview() {
    PreviewCardSurface {
        StatCard(
            stat = WeatherStatUiModel(
                label = "Wind speed",
                value = "22 km/h",
                type = WeatherStatType.Wind,
            ),
        )
    }
}

@Preview(name = "Location Context", showBackground = true)
@Composable
private fun LocationContextCardPreview() {
    PreviewCardSurface {
        val weather = previewWeatherUiModel()
        LocationContextCard(
            mapCoordinates = weather.mapCoordinates,
            coordinates = weather.coordinates,
            title = weather.title,
            showLocationMap = false,
        )
    }
}

@Composable
private fun PreviewCardSurface(content: @Composable () -> Unit) {
    FeverTestTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp),
        ) {
            content()
        }
    }
}

private fun previewWeatherUiModel(): WeatherUiModel = WeatherUiModel(
    title = "Nuuk, GL",
    coordinates = "64.1835° N, 51.7216° W",
    mapCoordinates = GeoCoordinates(64.1835, -51.7216),
    temperature = "-4°",
    unitLabel = "C",
    condition = "Light Snow",
    feelsLike = "Feels like -9°C",
    visibility = "10.0 km",
    localTime = "11:42",
    stats = listOf(
        WeatherStatUiModel("Humidity", "78%", WeatherStatType.Humidity),
        WeatherStatUiModel("Wind speed", "22 km/h", WeatherStatType.Wind),
        WeatherStatUiModel("Pressure", "1012 hPa", WeatherStatType.Pressure),
        WeatherStatUiModel("Cloud cover", "92%", WeatherStatType.CloudCover),
    ),
    conditionType = WeatherConditionType.Snow,
)

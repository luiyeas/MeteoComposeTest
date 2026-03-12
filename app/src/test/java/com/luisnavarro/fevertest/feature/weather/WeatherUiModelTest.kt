package com.luisnavarro.fevertest.feature.weather

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherUiModelTest {

    @Test
    fun `toUiModel uses city and country when both are available`() {
        val uiModel = sampleWeatherData(
            locationName = "Nuuk",
            countryCode = "GL",
        ).toUiModel()

        assertEquals("Nuuk, GL", uiModel.title)
    }

    @Test
    fun `toUiModel uses country when city is missing`() {
        val uiModel = sampleWeatherData(
            locationName = null,
            countryCode = "GL",
        ).toUiModel()

        assertEquals("GL", uiModel.title)
    }

    @Test
    fun `toUiModel falls back to random location when city and country are missing`() {
        val uiModel = sampleWeatherData(
            locationName = null,
            countryCode = null,
        ).toUiModel()

        assertEquals("Random location", uiModel.title)
    }
}

private fun sampleWeatherData(
    locationName: String?,
    countryCode: String?,
): CurrentWeatherData = CurrentWeatherData(
    coordinates = GeoCoordinates(
        latitude = 64.1835,
        longitude = -51.7216,
    ),
    locationName = locationName,
    countryCode = countryCode,
    conditionSummary = "Snow",
    conditionDescription = "light snow",
    temperatureCelsius = -4.2,
    feelsLikeCelsius = -9.4,
    visibilityKilometers = 10.0,
    observedAtEpochSeconds = 1_731_322_800L,
    timezoneOffsetSeconds = -10_800,
    humidityPercent = 78,
    windSpeedKilometersPerHour = 22,
    pressureHpa = 1012,
    cloudCoverPercent = 92,
)

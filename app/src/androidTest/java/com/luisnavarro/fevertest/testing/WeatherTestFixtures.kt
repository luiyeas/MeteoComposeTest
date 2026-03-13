package com.luisnavarro.fevertest.testing

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import com.luisnavarro.fevertest.feature.weather.WeatherUiModel
import com.luisnavarro.fevertest.feature.weather.toUiModel

val NuukCoordinates: GeoCoordinates = GeoCoordinates(64.1835, -51.7216)
val SydneyCoordinates: GeoCoordinates = GeoCoordinates(-33.8688, 151.2093)

fun nuukWeatherData(): CurrentWeatherData = weatherData(
    coordinates = NuukCoordinates,
    locationName = "Nuuk",
    countryCode = "GL",
    conditionSummary = "Snow",
    conditionDescription = "light snow",
    temperatureCelsius = -4.0,
    feelsLikeCelsius = -9.0,
    visibilityKilometers = 10.0,
    observedAtEpochSeconds = 1_741_774_320,
    timezoneOffsetSeconds = -7_200,
    humidityPercent = 78,
    windSpeedKilometersPerHour = 22,
    pressureHpa = 1_012,
    cloudCoverPercent = 92,
)

fun sydneyWeatherData(): CurrentWeatherData = weatherData(
    coordinates = SydneyCoordinates,
    locationName = "Sydney",
    countryCode = "AU",
    conditionSummary = "Clear",
    conditionDescription = "clear sky",
    temperatureCelsius = 27.0,
    feelsLikeCelsius = 29.0,
    visibilityKilometers = 10.0,
    observedAtEpochSeconds = 1_741_774_320,
    timezoneOffsetSeconds = 39_600,
    humidityPercent = 63,
    windSpeedKilometersPerHour = 18,
    pressureHpa = 1_008,
    cloudCoverPercent = 4,
)

fun sampleWeatherUiModel(): WeatherUiModel = nuukWeatherData().toUiModel()

private fun weatherData(
    coordinates: GeoCoordinates,
    locationName: String,
    countryCode: String,
    conditionSummary: String,
    conditionDescription: String,
    temperatureCelsius: Double,
    feelsLikeCelsius: Double,
    visibilityKilometers: Double,
    observedAtEpochSeconds: Long,
    timezoneOffsetSeconds: Int,
    humidityPercent: Int,
    windSpeedKilometersPerHour: Int,
    pressureHpa: Int,
    cloudCoverPercent: Int,
): CurrentWeatherData = CurrentWeatherData(
    coordinates = coordinates,
    locationName = locationName,
    countryCode = countryCode,
    conditionSummary = conditionSummary,
    conditionDescription = conditionDescription,
    temperatureCelsius = temperatureCelsius,
    feelsLikeCelsius = feelsLikeCelsius,
    visibilityKilometers = visibilityKilometers,
    observedAtEpochSeconds = observedAtEpochSeconds,
    timezoneOffsetSeconds = timezoneOffsetSeconds,
    humidityPercent = humidityPercent,
    windSpeedKilometersPerHour = windSpeedKilometersPerHour,
    pressureHpa = pressureHpa,
    cloudCoverPercent = cloudCoverPercent,
)

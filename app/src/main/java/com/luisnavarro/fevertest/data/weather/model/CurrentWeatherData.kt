package com.luisnavarro.fevertest.data.weather.model

import com.luisnavarro.fevertest.core.model.GeoCoordinates

data class CurrentWeatherData(
    val coordinates: GeoCoordinates,
    val locationName: String?,
    val countryCode: String?,
    val conditionSummary: String,
    val conditionDescription: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val visibilityKilometers: Double?,
    val observedAtEpochSeconds: Long,
    val timezoneOffsetSeconds: Int,
    val humidityPercent: Int?,
    val windSpeedKilometersPerHour: Int?,
    val pressureHpa: Int?,
    val cloudCoverPercent: Int?,
)

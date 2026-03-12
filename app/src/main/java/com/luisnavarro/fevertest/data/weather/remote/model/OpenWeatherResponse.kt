package com.luisnavarro.fevertest.data.weather.remote.model

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    val coord: CoordinatesResponse?,
    val weather: List<WeatherConditionResponse>,
    val main: MainWeatherResponse,
    val visibility: Int?,
    val wind: WindResponse?,
    val clouds: CloudsResponse?,
    @SerializedName("dt") val observedAtEpochSeconds: Long,
    @SerializedName("timezone") val timezoneOffsetSeconds: Int,
    @SerializedName("sys") val system: SystemResponse?,
    val name: String?,
)

data class CoordinatesResponse(
    @SerializedName("lat") val latitude: Double?,
    @SerializedName("lon") val longitude: Double?,
)

data class WeatherConditionResponse(
    val main: String?,
    val description: String?,
)

data class MainWeatherResponse(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int?,
    val pressure: Int?,
)

data class WindResponse(
    @SerializedName("speed") val speedMetersPerSecond: Double?,
)

data class CloudsResponse(
    @SerializedName("all") val cloudCoverPercent: Int?,
)

data class SystemResponse(
    @SerializedName("country") val countryCode: String?,
)

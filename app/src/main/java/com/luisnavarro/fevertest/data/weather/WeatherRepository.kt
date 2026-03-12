package com.luisnavarro.fevertest.data.weather

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import com.luisnavarro.fevertest.data.weather.remote.OpenWeatherApi
import com.luisnavarro.fevertest.data.weather.remote.model.OpenWeatherResponse
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.roundToInt

interface WeatherRepository {
    suspend fun getCurrentWeather(location: GeoCoordinates): CurrentWeatherData
}

@Singleton
class DefaultWeatherRepository @Inject constructor(
    private val api: OpenWeatherApi,
    @param:Named("openWeatherApiKey")
    private val apiKey: String,
) : WeatherRepository {

    override suspend fun getCurrentWeather(location: GeoCoordinates): CurrentWeatherData {
        val response = api.getCurrentWeather(
            latitude = location.latitude,
            longitude = location.longitude,
            apiKey = apiKey,
        )
        return response.toCurrentWeatherData(fallbackCoordinates = location)
    }
}

internal fun OpenWeatherResponse.toCurrentWeatherData(
    fallbackCoordinates: GeoCoordinates,
): CurrentWeatherData {
    val primaryWeather = weather.firstOrNull()
        ?: error("Weather condition unavailable for the selected location.")

    return CurrentWeatherData(
        coordinates = GeoCoordinates(
            latitude = coord?.latitude ?: fallbackCoordinates.latitude,
            longitude = coord?.longitude ?: fallbackCoordinates.longitude,
        ),
        locationName = name?.takeIf(String::isNotBlank),
        countryCode = system?.countryCode?.takeIf(String::isNotBlank),
        conditionSummary = primaryWeather.main.orEmpty(),
        conditionDescription = primaryWeather.description.orEmpty(),
        temperatureCelsius = main.temperature,
        feelsLikeCelsius = main.feelsLike,
        visibilityKilometers = visibility?.div(1000.0),
        observedAtEpochSeconds = observedAtEpochSeconds,
        timezoneOffsetSeconds = timezoneOffsetSeconds,
        humidityPercent = main.humidity,
        windSpeedKilometersPerHour = wind?.speedMetersPerSecond?.times(3.6)?.roundToInt(),
        pressureHpa = main.pressure,
        cloudCoverPercent = clouds?.cloudCoverPercent,
    )
}

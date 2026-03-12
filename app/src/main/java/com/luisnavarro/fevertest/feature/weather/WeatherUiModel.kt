package com.luisnavarro.fevertest.feature.weather

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

data class WeatherUiModel(
    val title: String,
    val coordinates: String,
    val mapCoordinates: GeoCoordinates,
    val temperature: String,
    val unitLabel: String,
    val condition: String,
    val feelsLike: String,
    val visibility: String,
    val localTime: String,
    val stats: List<WeatherStatUiModel>,
    val conditionType: WeatherConditionType,
)

data class WeatherStatUiModel(
    val label: String,
    val value: String,
    val type: WeatherStatType,
)

enum class WeatherConditionType {
    Clear,
    Clouds,
    Rain,
    Snow,
    Storm,
    Atmosphere,
    Drizzle,
    Unknown,
}

enum class WeatherStatType {
    Humidity,
    Wind,
    Pressure,
    CloudCover,
}

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

fun CurrentWeatherData.toUiModel(): WeatherUiModel = WeatherUiModel(
    title = buildLocationTitle(),
    coordinates = coordinates.toDisplayString(),
    mapCoordinates = coordinates,
    temperature = temperatureCelsius.toTemperatureValue(),
    unitLabel = "C",
    condition = conditionDescription.toDisplayCondition(),
    feelsLike = "Feels like ${feelsLikeCelsius.toRoundedTemperature()}°C",
    visibility = visibilityKilometers?.let { String.format(Locale.US, "%.1f km", it) } ?: "N/A",
    localTime = buildLocalTime(),
    stats = listOf(
        WeatherStatUiModel(
            label = "Humidity",
            value = humidityPercent?.let { "$it%" } ?: "N/A",
            type = WeatherStatType.Humidity,
        ),
        WeatherStatUiModel(
            label = "Wind speed",
            value = windSpeedKilometersPerHour?.let { "$it km/h" } ?: "N/A",
            type = WeatherStatType.Wind,
        ),
        WeatherStatUiModel(
            label = "Pressure",
            value = pressureHpa?.let { "$it hPa" } ?: "N/A",
            type = WeatherStatType.Pressure,
        ),
        WeatherStatUiModel(
            label = "Cloud cover",
            value = cloudCoverPercent?.let { "$it%" } ?: "N/A",
            type = WeatherStatType.CloudCover,
        ),
    ),
    conditionType = conditionSummary.toConditionType(),
)

private fun CurrentWeatherData.buildLocationTitle(): String {
    val country = countryCode?.takeIf(String::isNotBlank)
    val location = locationName?.takeIf(String::isNotBlank)

    return when {
        location != null && country != null -> "$location, $country"
        location != null -> location
        country != null -> country
        else -> "Random location"
    }
}

private fun CurrentWeatherData.buildLocalTime(): String {
    val localInstant = Instant.ofEpochSecond(observedAtEpochSeconds + timezoneOffsetSeconds.toLong())
    return timeFormatter.format(localInstant.atOffset(ZoneOffset.UTC))
}

private fun Double.toTemperatureValue(): String = "${toRoundedTemperature()}°"

private fun Double.toRoundedTemperature(): Int = roundToInt()

private fun GeoCoordinates.toDisplayString(): String {
    val latitudeLabel = formatCoordinate(latitude, positiveLabel = "N", negativeLabel = "S")
    val longitudeLabel = formatCoordinate(longitude, positiveLabel = "E", negativeLabel = "W")
    return "$latitudeLabel, $longitudeLabel"
}

private fun formatCoordinate(
    value: Double,
    positiveLabel: String,
    negativeLabel: String,
): String {
    val label = if (value >= 0) positiveLabel else negativeLabel
    val formattedValue = String.format(Locale.US, "%.4f", abs(value))
    return "$formattedValue° $label"
}

private fun String.toDisplayCondition(): String {
    if (isBlank()) return "Current conditions"
    return trim()
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase(Locale.US).replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) {
                    firstChar.titlecase(Locale.US)
                } else {
                    firstChar.toString()
                }
            }
        }
}

private fun String.toConditionType(): WeatherConditionType = when (lowercase(Locale.US)) {
    "clear" -> WeatherConditionType.Clear
    "clouds" -> WeatherConditionType.Clouds
    "rain" -> WeatherConditionType.Rain
    "snow" -> WeatherConditionType.Snow
    "thunderstorm" -> WeatherConditionType.Storm
    "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> {
        WeatherConditionType.Atmosphere
    }

    "drizzle" -> WeatherConditionType.Drizzle
    else -> WeatherConditionType.Unknown
}

package com.luisnavarro.fevertest.data.weather

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.remote.model.CloudsResponse
import com.luisnavarro.fevertest.data.weather.remote.model.CoordinatesResponse
import com.luisnavarro.fevertest.data.weather.remote.model.MainWeatherResponse
import com.luisnavarro.fevertest.data.weather.remote.model.OpenWeatherResponse
import com.luisnavarro.fevertest.data.weather.remote.model.SystemResponse
import com.luisnavarro.fevertest.data.weather.remote.model.WeatherConditionResponse
import com.luisnavarro.fevertest.data.weather.remote.model.WindResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class WeatherMapperTest {

    @Test
    fun `toCurrentWeatherData maps transport values into domain model`() {
        val response = OpenWeatherResponse(
            coord = CoordinatesResponse(
                latitude = 64.1835,
                longitude = -51.7216,
            ),
            weather = listOf(
                WeatherConditionResponse(
                    main = "Snow",
                    description = "light snow",
                )
            ),
            main = MainWeatherResponse(
                temperature = -4.2,
                feelsLike = -9.4,
                humidity = 78,
                pressure = 1012,
            ),
            visibility = 10_000,
            wind = WindResponse(speedMetersPerSecond = 6.1),
            clouds = CloudsResponse(cloudCoverPercent = 92),
            observedAtEpochSeconds = 1_731_322_800L,
            timezoneOffsetSeconds = -10_800,
            system = SystemResponse(countryCode = "GL"),
            name = "Nuuk",
        )

        val weather = response.toCurrentWeatherData(
            fallbackCoordinates = GeoCoordinates(
                latitude = 0.0,
                longitude = 0.0,
            )
        )

        assertEquals("Nuuk", weather.locationName)
        assertEquals("GL", weather.countryCode)
        assertEquals("Snow", weather.conditionSummary)
        assertEquals("light snow", weather.conditionDescription)
        assertEquals(-4.2, weather.temperatureCelsius, 0.0)
        assertEquals(-9.4, weather.feelsLikeCelsius, 0.0)
        assertEquals(10.0, weather.visibilityKilometers ?: 0.0, 0.0)
        assertEquals(22, weather.windSpeedKilometersPerHour)
        assertEquals(78, weather.humidityPercent)
        assertEquals(1012, weather.pressureHpa)
        assertEquals(92, weather.cloudCoverPercent)
        assertEquals(64.1835, weather.coordinates.latitude, 0.0)
        assertEquals(-51.7216, weather.coordinates.longitude, 0.0)
    }

    @Test
    fun `toCurrentWeatherData uses fallback coordinates and clears blank location metadata`() {
        val fallbackCoordinates = GeoCoordinates(
            latitude = 12.34,
            longitude = 56.78,
        )
        val response = OpenWeatherResponse(
            coord = null,
            weather = listOf(
                WeatherConditionResponse(
                    main = "Clear",
                    description = "clear sky",
                )
            ),
            main = MainWeatherResponse(
                temperature = 20.0,
                feelsLike = 21.0,
                humidity = 50,
                pressure = 1008,
            ),
            visibility = null,
            wind = null,
            clouds = null,
            observedAtEpochSeconds = 1_731_322_800L,
            timezoneOffsetSeconds = 0,
            system = SystemResponse(countryCode = ""),
            name = "",
        )

        val weather = response.toCurrentWeatherData(fallbackCoordinates = fallbackCoordinates)

        assertEquals(12.34, weather.coordinates.latitude, 0.0)
        assertEquals(56.78, weather.coordinates.longitude, 0.0)
        assertNull(weather.locationName)
        assertNull(weather.countryCode)
    }

    @Test
    fun `toCurrentWeatherData throws controlled exception when weather list is empty`() {
        val response = OpenWeatherResponse(
            coord = CoordinatesResponse(
                latitude = 64.1835,
                longitude = -51.7216,
            ),
            weather = emptyList(),
            main = MainWeatherResponse(
                temperature = -4.2,
                feelsLike = -9.4,
                humidity = 78,
                pressure = 1012,
            ),
            visibility = 10_000,
            wind = WindResponse(speedMetersPerSecond = 6.1),
            clouds = CloudsResponse(cloudCoverPercent = 92),
            observedAtEpochSeconds = 1_731_322_800L,
            timezoneOffsetSeconds = -10_800,
            system = SystemResponse(countryCode = "GL"),
            name = "Nuuk",
        )

        val exception = assertThrows(MalformedWeatherPayloadException::class.java) {
            response.toCurrentWeatherData(
                fallbackCoordinates = GeoCoordinates(
                    latitude = 0.0,
                    longitude = 0.0,
                )
            )
        }

        assertEquals(
            "Weather condition unavailable for the selected location.",
            exception.message,
        )
    }
}

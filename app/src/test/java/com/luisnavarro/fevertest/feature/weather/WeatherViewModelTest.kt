package com.luisnavarro.fevertest.feature.weather

import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import com.luisnavarro.fevertest.testing.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatchers: AppDispatchers
        get() = TestAppDispatchers(mainDispatcherRule.dispatcher)

    @Test
    fun `initial load fetches weather and exposes success state`() = runTest {
        val repository = FakeWeatherRepository(
            results = ArrayDeque(
                listOf(Result.success(sampleWeatherData(locationName = "Nuuk")))
            )
        )
        val generator = FakeLocationGenerator(
            coordinates = ArrayDeque(
                listOf(GeoCoordinates(64.1835, -51.7216))
            )
        )

        val viewModel = WeatherViewModel(repository, generator, dispatchers)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isInitialLoading)
        assertNotNull(uiState.weather)
        assertEquals("Nuuk, GL", uiState.weather?.title)
        assertEquals(1, repository.requestCount)
    }

    @Test
    fun `refresh keeps previous content visible while loading`() = runTest {
        val repository = FakeWeatherRepository(
            results = ArrayDeque(
                listOf(
                    Result.success(sampleWeatherData(locationName = "Nuuk")),
                    Result.success(sampleWeatherData(locationName = "Reykjavik")),
                )
            )
        )
        val generator = FakeLocationGenerator(
            coordinates = ArrayDeque(
                listOf(
                    GeoCoordinates(64.1835, -51.7216),
                    GeoCoordinates(64.1466, -21.9426),
                )
            )
        )

        val viewModel = WeatherViewModel(repository, generator, dispatchers)
        advanceUntilIdle()

        viewModel.onAction(WeatherUiAction.RefreshClicked)

        val loadingState = viewModel.uiState.value
        assertTrue(loadingState.isRefreshing)
        assertEquals("Nuuk, GL", loadingState.weather?.title)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isRefreshing)
        assertEquals("Reykjavik, IS", uiState.weather?.title)
        assertEquals(2, repository.requestCount)
    }

    @Test
    fun `initial failure exposes blocking error state`() = runTest {
        val repository = FakeWeatherRepository(
            results = ArrayDeque(
                listOf(Result.failure(IOException("Network down")))
            )
        )
        val generator = FakeLocationGenerator(
            coordinates = ArrayDeque(
                listOf(GeoCoordinates(64.1835, -51.7216))
            )
        )

        val viewModel = WeatherViewModel(repository, generator, dispatchers)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue(uiState.showBlockingError)
        assertEquals(
            "We couldn't reach the weather service. Please try again.",
            uiState.errorMessage,
        )
    }

    @Test
    fun `refresh failure preserves previous content and surfaces recoverable error`() = runTest {
        val repository = FakeWeatherRepository(
            results = ArrayDeque(
                listOf(
                    Result.success(sampleWeatherData(locationName = "Nuuk")),
                    Result.failure(IOException("Timeout")),
                )
            )
        )
        val generator = FakeLocationGenerator(
            coordinates = ArrayDeque(
                listOf(
                    GeoCoordinates(64.1835, -51.7216),
                    GeoCoordinates(10.0, 12.0),
                )
            )
        )

        val viewModel = WeatherViewModel(repository, generator, dispatchers)
        advanceUntilIdle()

        viewModel.onAction(WeatherUiAction.RefreshClicked)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals("Nuuk, GL", uiState.weather?.title)
        assertEquals(
            "We couldn't reach the weather service. Please try again.",
            uiState.errorMessage,
        )
        assertFalse(uiState.showBlockingError)
    }

    private class TestAppDispatchers(
        override val io: CoroutineDispatcher,
    ) : AppDispatchers

    private class FakeLocationGenerator(
        private val coordinates: ArrayDeque<GeoCoordinates>,
    ) : RandomLocationGenerator {
        override fun generate(): GeoCoordinates = coordinates.removeFirst()
    }

    private class FakeWeatherRepository(
        private val results: ArrayDeque<Result<CurrentWeatherData>>,
    ) : WeatherRepository {
        var requestCount: Int = 0
            private set

        override suspend fun getCurrentWeather(location: GeoCoordinates): CurrentWeatherData {
            requestCount += 1
            return results.removeFirst().getOrThrow()
        }
    }
}

private fun sampleWeatherData(locationName: String): CurrentWeatherData = CurrentWeatherData(
    coordinates = GeoCoordinates(
        latitude = 64.1835,
        longitude = -51.7216,
    ),
    locationName = locationName,
    countryCode = if (locationName == "Reykjavik") "IS" else "GL",
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

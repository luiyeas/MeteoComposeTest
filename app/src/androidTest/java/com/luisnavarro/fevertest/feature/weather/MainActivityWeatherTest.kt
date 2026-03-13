package com.luisnavarro.fevertest.feature.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.luisnavarro.fevertest.MainActivity
import com.luisnavarro.fevertest.testing.NuukCoordinates
import com.luisnavarro.fevertest.testing.SydneyCoordinates
import com.luisnavarro.fevertest.testing.fakes.FakeRandomLocationGenerator
import com.luisnavarro.fevertest.testing.fakes.FakeWeatherRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityWeatherTest {

    init {
        FakeWeatherRepository.useDefaultResponses()
        FakeRandomLocationGenerator.useDefaultLocations()
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loadsWeatherThroughHiltAndRefreshesWithNextRandomLocation() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithTag(WeatherScreenTestTags.LocationTitle)
                    .assertTextEquals("Nuuk, GL")
            }.isSuccess
        }

        composeRule.onNodeWithTag(WeatherScreenTestTags.LocationTitle)
            .assertIsDisplayed()
            .assertTextEquals("Nuuk, GL")

        composeRule.onNodeWithTag(WeatherScreenTestTags.RefreshFab).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithTag(WeatherScreenTestTags.LocationTitle)
                    .assertTextEquals("Sydney, AU")
            }.isSuccess
        }

        composeRule.onNodeWithTag(WeatherScreenTestTags.LocationTitle)
            .assertIsDisplayed()
            .assertTextEquals("Sydney, AU")
        assertEquals(listOf(NuukCoordinates, SydneyCoordinates), FakeWeatherRepository.requestedLocations)
    }
}

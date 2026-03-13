package com.luisnavarro.fevertest.feature.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.luisnavarro.fevertest.testing.sampleWeatherUiModel
import com.luisnavarro.fevertest.ui.theme.FeverTestTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsLoadingStateDuringInitialFetch() {
        composeRule.setContent {
            FeverTestTheme {
                WeatherScreen(
                    uiState = WeatherUiState(isInitialLoading = true),
                    onAction = {},
                    showLocationMap = false,
                )
            }
        }

        composeRule.onNodeWithTag(WeatherScreenTestTags.LoadingState).assertIsDisplayed()
        composeRule.onNodeWithText("Finding a random location").assertIsDisplayed()
        composeRule.onNodeWithTag(WeatherScreenTestTags.RefreshFab).assertIsDisplayed()
    }

    @Test
    fun dispatchesRetryFromBlockingErrorState() {
        var capturedAction: WeatherUiAction? = null

        composeRule.setContent {
            FeverTestTheme {
                WeatherScreen(
                    uiState = WeatherUiState(errorMessage = "Weather service unavailable"),
                    onAction = { capturedAction = it },
                    showLocationMap = false,
                )
            }
        }

        composeRule.onNodeWithTag(WeatherScreenTestTags.BlockingError).assertIsDisplayed()
        composeRule.onNodeWithText("Try again").performClick()

        assertEquals(WeatherUiAction.RetryClicked, capturedAction)
    }

    @Test
    fun rendersContentAndDispatchesRefreshFromFab() {
        var capturedAction: WeatherUiAction? = null

        composeRule.setContent {
            FeverTestTheme {
                WeatherScreen(
                    uiState = WeatherUiState(
                        weather = sampleWeatherUiModel(),
                        errorMessage = "Showing the last successful result.",
                    ),
                    onAction = { capturedAction = it },
                    showLocationMap = false,
                )
            }
        }

        composeRule.onNodeWithTag(WeatherScreenTestTags.WeatherContent).assertIsDisplayed()
        composeRule.onNodeWithTag(WeatherScreenTestTags.InlineErrorBanner).assertIsDisplayed()
        composeRule.onNodeWithTag(WeatherScreenTestTags.LocationCard).assertIsDisplayed()
        composeRule.onNodeWithTag(WeatherScreenTestTags.LocationMapPlaceholder).assertIsDisplayed()
        composeRule.onNodeWithTag(WeatherScreenTestTags.LocationTitle)
            .assertIsDisplayed()
            .assertTextEquals("Nuuk, GL")

        composeRule.onNodeWithTag(WeatherScreenTestTags.RefreshFab).performClick()

        assertEquals(WeatherUiAction.RefreshClicked, capturedAction)
    }
}

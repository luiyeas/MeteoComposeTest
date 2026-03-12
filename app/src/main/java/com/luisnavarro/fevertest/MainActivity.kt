package com.luisnavarro.fevertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.luisnavarro.fevertest.feature.weather.WeatherRoute
import com.luisnavarro.fevertest.ui.theme.FeverTestTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy { AppContainer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeverTestTheme {
                Surface {
                    WeatherRoute(
                        factory = appContainer.weatherViewModelFactory(),
                    )
                }
            }
        }
    }
}

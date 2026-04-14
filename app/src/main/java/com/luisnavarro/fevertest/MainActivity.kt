package com.luisnavarro.fevertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.luisnavarro.fevertest.feature.weather.WeatherFeatureConfig
import com.luisnavarro.fevertest.feature.weather.WeatherRoute
import com.luisnavarro.fevertest.ui.theme.FeverTestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var weatherFeatureConfig: WeatherFeatureConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeverTestTheme {
                Surface {
                    WeatherRoute(showLocationMap = weatherFeatureConfig.showLocationMap)
                }
            }
        }
    }
}

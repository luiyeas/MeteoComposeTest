package com.luisnavarro.fevertest

import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.core.dispatchers.DefaultAppDispatchers
import com.luisnavarro.fevertest.data.location.DefaultRandomLocationGenerator
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.DefaultWeatherRepository
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.data.weather.remote.OpenWeatherApi
import com.luisnavarro.fevertest.feature.weather.WeatherViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val openWeatherApi: OpenWeatherApi = retrofit.create(OpenWeatherApi::class.java)

    private val weatherRepository: WeatherRepository = DefaultWeatherRepository(
        api = openWeatherApi,
        apiKey = BuildConfig.OPEN_WEATHER_API_KEY,
    )

    private val locationGenerator: RandomLocationGenerator = DefaultRandomLocationGenerator()

    private val dispatchers: AppDispatchers = DefaultAppDispatchers

    fun weatherViewModelFactory(): WeatherViewModelFactory = WeatherViewModelFactory(
        repository = weatherRepository,
        locationGenerator = locationGenerator,
        dispatchers = dispatchers,
    )
}

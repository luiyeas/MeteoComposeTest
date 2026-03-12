package com.luisnavarro.fevertest

import android.util.Log
import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.core.dispatchers.DefaultAppDispatchers
import com.luisnavarro.fevertest.data.location.DefaultRandomLocationGenerator
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.DefaultWeatherRepository
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.data.weather.remote.OpenWeatherApi
import com.luisnavarro.fevertest.feature.weather.WeatherViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor { message ->
                        Log.d("OpenWeatherHttp", message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                        redactQueryParams("appid")
                    }
                )
            }
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(okHttpClient)
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

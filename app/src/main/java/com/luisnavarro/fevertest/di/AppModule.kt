package com.luisnavarro.fevertest.di

import android.util.Log
import com.luisnavarro.fevertest.BuildConfig
import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.core.dispatchers.DefaultAppDispatchers
import com.luisnavarro.fevertest.data.location.DefaultRandomLocationGenerator
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.DefaultWeatherRepository
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.data.weather.remote.OpenWeatherApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
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

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenWeatherApi(retrofit: Retrofit): OpenWeatherApi =
        retrofit.create(OpenWeatherApi::class.java)

    @Provides
    @Singleton
    @Named("openWeatherApiKey")
    fun provideOpenWeatherApiKey(): String = BuildConfig.OPEN_WEATHER_API_KEY

    @Provides
    @Singleton
    fun provideRandomLocationGenerator(): RandomLocationGenerator = DefaultRandomLocationGenerator()

    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers = DefaultAppDispatchers
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingsModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        implementation: DefaultWeatherRepository,
    ): WeatherRepository
}

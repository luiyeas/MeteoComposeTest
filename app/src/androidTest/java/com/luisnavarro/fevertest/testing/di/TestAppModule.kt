package com.luisnavarro.fevertest.testing.di

import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.di.AppBindingsModule
import com.luisnavarro.fevertest.di.AppModule
import com.luisnavarro.fevertest.feature.weather.WeatherFeatureConfig
import com.luisnavarro.fevertest.testing.fakes.FakeRandomLocationGenerator
import com.luisnavarro.fevertest.testing.fakes.FakeWeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class, AppBindingsModule::class],
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(): WeatherRepository = FakeWeatherRepository

    @Provides
    @Singleton
    fun provideRandomLocationGenerator(): RandomLocationGenerator = FakeRandomLocationGenerator

    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers = object : AppDispatchers {
        override val io = Dispatchers.Main.immediate
    }

    @Provides
    fun provideWeatherFeatureConfig(): WeatherFeatureConfig = WeatherFeatureConfig(
        showLocationMap = false,
    )
}

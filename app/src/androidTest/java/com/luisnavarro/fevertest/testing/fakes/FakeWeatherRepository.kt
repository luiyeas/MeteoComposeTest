package com.luisnavarro.fevertest.testing.fakes

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import com.luisnavarro.fevertest.data.weather.model.CurrentWeatherData
import com.luisnavarro.fevertest.testing.NuukCoordinates
import com.luisnavarro.fevertest.testing.SydneyCoordinates
import com.luisnavarro.fevertest.testing.nuukWeatherData
import com.luisnavarro.fevertest.testing.sydneyWeatherData

object FakeWeatherRepository : WeatherRepository {
    private var responseProvider: suspend (GeoCoordinates) -> CurrentWeatherData = ::defaultResponse

    val requestedLocations: MutableList<GeoCoordinates> = mutableListOf()

    override suspend fun getCurrentWeather(location: GeoCoordinates): CurrentWeatherData {
        requestedLocations += location
        return responseProvider(location)
    }

    fun enqueueSuccess(data: CurrentWeatherData) {
        responseProvider = { data }
    }

    fun enqueueFailure(throwable: Throwable) {
        responseProvider = { throw throwable }
    }

    fun useDefaultResponses() {
        requestedLocations.clear()
        responseProvider = ::defaultResponse
    }

    private fun defaultResponse(location: GeoCoordinates): CurrentWeatherData = when (location) {
        NuukCoordinates -> nuukWeatherData()
        SydneyCoordinates -> sydneyWeatherData()
        else -> nuukWeatherData().copy(coordinates = location, locationName = null, countryCode = null)
    }
}

package com.luisnavarro.fevertest.testing.fakes

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.testing.NuukCoordinates
import com.luisnavarro.fevertest.testing.SydneyCoordinates

object FakeRandomLocationGenerator : RandomLocationGenerator {
    private var queuedLocations: ArrayDeque<GeoCoordinates> = defaultLocations()

    override fun generate(): GeoCoordinates = queuedLocations.removeFirstOrNull()
        ?: SydneyCoordinates

    fun useDefaultLocations() {
        queuedLocations = defaultLocations()
    }

    fun setLocations(locations: List<GeoCoordinates>) {
        queuedLocations = ArrayDeque(locations)
    }

    private fun defaultLocations(): ArrayDeque<GeoCoordinates> = ArrayDeque(
        listOf(
            NuukCoordinates,
            SydneyCoordinates,
        )
    )
}

package com.luisnavarro.fevertest.data.location

import com.luisnavarro.fevertest.core.model.GeoCoordinates
import kotlin.random.Random

fun interface RandomLocationGenerator {
    fun generate(): GeoCoordinates
}

class DefaultRandomLocationGenerator(
    private val random: Random = Random.Default,
) : RandomLocationGenerator {

    override fun generate(): GeoCoordinates = GeoCoordinates(
        latitude = random.nextDouble(from = -90.0, until = 90.0),
        longitude = random.nextDouble(from = -180.0, until = 180.0),
    )
}

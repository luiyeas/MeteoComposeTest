package com.luisnavarro.fevertest.data.location

import kotlin.random.Random
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultRandomLocationGeneratorTest {

    @Test
    fun `generate returns coordinates inside valid geographic bounds`() {
        val generator = DefaultRandomLocationGenerator(random = Random(1234))

        repeat(500) {
            val coordinates = generator.generate()

            assertTrue(coordinates.latitude in -90.0..90.0)
            assertTrue(coordinates.longitude in -180.0..180.0)
        }
    }
}

package utils

import kotlin.test.Test
import com.dcop.utils.DistanceCalculator
import com.dcop.utils.DistanceCalculatorStrategies
import com.google.common.truth.Truth.assertThat
//import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DistanceCalculatorTest {

    private val haversineStrategy = DistanceCalculatorStrategies.haversine
    private val pythagorasStrategy = DistanceCalculatorStrategies.pythagoras

    @Test
    fun `test haversine strategy calculates correct distance`() {
        val calculator = DistanceCalculator(haversineStrategy)

        val userLat = 59.3293
        val userLon = 18.0686
        val venueLat = 59.3489
        val venueLon = 18.0686

        val distance = calculator.calculateDistance(userLat, userLon, venueLat, venueLon)

        // Expected distance from Stockholm City Center to another location in Stockholm
        assertThat(distance).isAtLeast(2150) // Validate against a reasonable range
        assertThat(distance).isAtMost(2200)
    }

    @Test
    fun `test pythagoras strategy calculates correct distance`() {
        val calculator = DistanceCalculator(pythagorasStrategy)

        val userLat = 59.3293
        val userLon = 18.0686
        val venueLat = 59.3489
        val venueLon = 18.0686

        val distance = calculator.calculateDistance(userLat, userLon, venueLat, venueLon)

        assertThat(distance).isGreaterThan(2100) // Similar distance but slightly different calculation
        assertThat(distance).isLessThan(2300)
    }

    @Test
    fun `test invalid latitude throws exception`() {
        val calculator = DistanceCalculator(haversineStrategy)

        assertFailsWith<IllegalArgumentException> {
            calculator.calculateDistance(100.0, 18.0686, 59.3489, 18.0686) // Invalid latitude
        }
    }

    @Test
    fun `test invalid longitude throws exception`() {
        val calculator = DistanceCalculator(haversineStrategy)

        assertFailsWith<IllegalArgumentException> {
            calculator.calculateDistance(59.3293, 200.0, 59.3489, 18.0686) // Invalid longitude
        }
    }

    @Test
    fun `test strategy can be changed`() {
        val calculator = DistanceCalculator(haversineStrategy)
        calculator.setStrategy(pythagorasStrategy)

        val userLat = 59.3293
        val userLon = 18.0686
        val venueLat = 59.3489
        val venueLon = 18.0686

        val distance = calculator.calculateDistance(userLat, userLon, venueLat, venueLon)

        assertThat(distance).isGreaterThan(2100) // Verify with the Pythagoras strategy
        assertThat(distance).isLessThan(2300)
    }
}

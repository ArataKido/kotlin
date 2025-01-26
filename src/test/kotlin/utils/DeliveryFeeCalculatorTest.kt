package utils

import com.dcop.exceptions.OutOfRangeException
import kotlin.test.Test
import com.dcop.models.DistanceRange
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DeliveryFeeCalculatorStrategies
import com.dcop.utils.PriceFunction
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DeliveryFeeCalculatorTest {
    private val defaultStrategy = DeliveryFeeCalculatorStrategies.default

    @Test
    fun `test delivery fee calculation for valid range`() {
        val calculator = DeliveryFeeCalculator(defaultStrategy)

        val basePrice = 50
        val distance = 5000 // 5 km
        val distanceRanges = listOf(
            DistanceRange(0, 3000, 20, 1.5), // Below 3 km
            DistanceRange(3000, 7000, 30, 2.0), // Between 3-7 km
            DistanceRange(7000, 0, 50, 3.0) // Above 7 km
        )

        val fee = calculator.calculateDeliveryFee(basePrice, distance, distanceRanges)

        assertThat(fee).isEqualTo(30 + basePrice + (2.0 * 5000 / 10).toInt()) // Verify correct fee
    }

    @Test
    fun `test delivery fee throws exception for out of range`() {
        val calculator = DeliveryFeeCalculator(defaultStrategy)

        val basePrice = 50
        val distance = 9000 // Out of defined range
        val distanceRanges = listOf(
            DistanceRange(0, 3000, 20, 1.5), // Below 3 km
            DistanceRange(3000, 7000, 30, 2.0) // Between 3-7 km
        )

        assertFailsWith<OutOfRangeException> {
            calculator.calculateDeliveryFee(basePrice, distance, distanceRanges)
        }.also { exception ->
            assertThat(exception.statusCode).isEqualTo(400)
            assertThat(exception.message).isEqualTo("Delivery not possible for the given distance: $distance meters")
        }
    }

    @Test
    fun `test strategy can be changed`() {
        val mockStrategy = mockk<PriceFunction>()
        every { mockStrategy (any(), any(), any()) } returns 123


        val calculator = DeliveryFeeCalculator(defaultStrategy)
        calculator.setStrategy(mockStrategy)

        val fee = calculator.calculateDeliveryFee(50, 5000, emptyList())

        assertThat(fee).isEqualTo(123)

        verify { mockStrategy(50, 5000, emptyList()) }
    }
}

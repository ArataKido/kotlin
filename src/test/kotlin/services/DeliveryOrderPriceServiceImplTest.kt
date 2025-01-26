package services


//import org.junit.jupiter.api.Test
import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.models.*
import com.dcop.services.DeliveryOrderPriceServiceImpl
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DistanceCalculator
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.slf4j.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryOrderPriceServiceImplTest {

    private val mockClient = mockk<HomeAssignmentApiClient>()
    private val mockDistanceCalculator = mockk<DistanceCalculator>()
    private val mockDeliveryFeeCalculator = mockk<DeliveryFeeCalculator>()
    private val mockLogger = mockk<Logger>(relaxed = true)

    private lateinit var service: DeliveryOrderPriceServiceImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        startKoin {
            modules(module {
                single { mockClient }
                single { mockDistanceCalculator }
                single { mockDeliveryFeeCalculator }
                single { mockLogger }
                single { Dispatchers.Default } // Ensure Default is mocked for coroutines
            })
        }
        service = DeliveryOrderPriceServiceImpl(
            client = mockClient,
            distanceCalculator = mockDistanceCalculator,
            deliveryFeeCalculator = mockDeliveryFeeCalculator
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `calculateDeliveryOrderPrice - returns correct response`() = runTest(testDispatcher) {
        // Mock input
        val venueSlug = "test-venue"
        val cartValue = 700
        val userLat = 59.3293
        val userLon = 18.0686
        val venueLat = 59.3489
        val venueLon = 18.0686
        val distance = 2000

        val staticData = VenueStaticResponse(
            staticVenueRaw = StaticVenueRaw(
                location = Location(
                    coordinates = listOf(venueLon, venueLat)
                )
            )
        )

        val dynamicData = VenueDynamicResponse(
            dynamicVenueRaw = DynamicVenueRaw(
                deliverySpecs = DeliverySpecs(
                    orderMinimumNoSurcharge = 600,
                    deliveryPricing = DeliveryPricing(
                        basePrice = 100,
                        distanceRanges = listOf(
                            DistanceRange(0, 3000, 20, 1.5)
                        )
                    )
                )
            )
        )

        // Mock dependencies
        coEvery { mockClient.fetchVenueData(venueSlug) } returns VenueData(staticData, dynamicData)
        every { mockDistanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon) } returns distance
        every {
            mockDeliveryFeeCalculator.calculateDeliveryFee(
                100,
                distance,
                dynamicData.dynamicVenueRaw.deliverySpecs.deliveryPricing.distanceRanges
            )
        } returns 250

        // Act
        val result = service.calculateDeliveryOrderPrice(venueSlug, cartValue, userLat, userLon)

        // Assert
        assertThat(result.delivery.distance).isEqualTo(distance)
        assertThat(result.cartValue).isEqualTo(cartValue)
        assertThat(result.delivery.fee).isEqualTo(250)
        assertThat(result.smallOrderSurcharge).isEqualTo(0)
        assertThat(result.totalPrice).isEqualTo(950) // 700 (cart) + 250 (delivery) + 0 (surcharge)

        coVerify { mockClient.fetchVenueData(venueSlug) }
        verify { mockDistanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon) }
        verify {
            mockDeliveryFeeCalculator.calculateDeliveryFee(
                100,
                distance,
                dynamicData.dynamicVenueRaw.deliverySpecs.deliveryPricing.distanceRanges
            )
        }
    }

    @Test
    fun `calculateDeliveryOrderPrice - applies small order surcharge`() = runTest(testDispatcher) {
        // Mock input
        val venueSlug = "test-venue"
        val cartValue = 400
        val userLat = 59.3293
        val userLon = 18.0686
        val venueLat = 59.3489
        val venueLon = 18.0686
        val distance = 2000

        val staticData = VenueStaticResponse(
            staticVenueRaw = StaticVenueRaw(
                location = Location(
                    coordinates = listOf(venueLon, venueLat)
                )
            )
        )

        val dynamicData = VenueDynamicResponse(
            dynamicVenueRaw = DynamicVenueRaw(
                deliverySpecs = DeliverySpecs(
                    orderMinimumNoSurcharge = 600,
                    deliveryPricing = DeliveryPricing(
                        basePrice = 100,
                        distanceRanges = listOf(
                            DistanceRange(0, 3000, 20, 1.5)
                        )
                    )
                )
            )
        )

        // Mock dependencies
        coEvery { mockClient.fetchVenueData(venueSlug) } returns VenueData(staticData, dynamicData)
        every { mockDistanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon) } returns distance
        every {
            mockDeliveryFeeCalculator.calculateDeliveryFee(
                100,
                distance,
                dynamicData.dynamicVenueRaw.deliverySpecs.deliveryPricing.distanceRanges
            )
        } returns 250

        // Act
        val result = service.calculateDeliveryOrderPrice(venueSlug, cartValue, userLat, userLon)

        // Assert
        assertThat(result.totalPrice).isEqualTo(850) // 400 (cart) + 250 (delivery) + 200 (surcharge)
        assertThat(result.smallOrderSurcharge).isEqualTo(200)
    }
}

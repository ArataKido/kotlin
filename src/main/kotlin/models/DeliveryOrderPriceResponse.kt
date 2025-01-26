package com.dcop.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
/**
 * Price specifications for order delivery .
 *
 * @property totalPrice .
 * @property smallOrderSurcharge .
 * @property cartValue .
 */
data class DeliveryOrderPriceResponse(
    @SerialName("total_price") val totalPrice: Int,
    @SerialName("small_order_surcharge") val smallOrderSurcharge: Int,
    @SerialName("cart_value") val cartValue: Int,
    val delivery: Delivery,
)

/**
 * Delivery specifications.
 *
 * @property fee .
 * @property distance .
 */
@Serializable
data class Delivery(
    val fee: Int,
    val distance: Int
)




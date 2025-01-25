package com.dcop.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryOrderPriceResponse(
    @SerialName("total_price") val totalPrice: Int,
    @SerialName("small_order_surcharge") val smallOrderSurcharge: Int,
    @SerialName("cart_value") val cartValue: Int,
    val delivery: Delivery,
) {
    @Serializable
    data class Delivery(
        val fee: Int,
        val distance: Int
    )
}



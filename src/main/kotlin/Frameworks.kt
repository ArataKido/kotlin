package com.dcop

import com.dcop.services.DeliveryOrderPriceService
import com.dcop.services.DeliveryOrderPriceServiceImpl
import org.koin.dsl.module

val appModule = module {
    single<DeliveryOrderPriceService>(createdAtStart = true) { DeliveryOrderPriceServiceImpl() }
}


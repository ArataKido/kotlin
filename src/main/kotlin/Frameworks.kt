package com.dcop

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.services.DeliveryOrderPriceService
import com.dcop.services.DeliveryOrderPriceServiceImpl
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DeliveryFeeCalculatorStrategies
import com.dcop.utils.DistanceCalculator
import com.dcop.utils.DistanceCalculatorStrategies
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(appModule())
    }
}

fun appModule() = module {
    single { Config }
    single { get<Config>().httpClient }
    single { get<Config>().apiBaseUrl }
    single { get<Config>().maxRetries }
    single { DistanceCalculator(DistanceCalculatorStrategies.haversine) }
    single { DeliveryFeeCalculator(DeliveryFeeCalculatorStrategies.default) }
    single { HomeAssignmentApiClient(get()) }
    single<DeliveryOrderPriceService> { DeliveryOrderPriceServiceImpl(get(), get(), get()) }
//    single { KoinLogger() as Logger }
}


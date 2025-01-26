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

/**
 * Configures the application frameworks, including dependency injection with Koin.
 */
fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(appModule())
    }
    monitor.subscribe(ApplicationStopping) {
        println("Shutting down HttpClient...")
        Config.closeHttpClient() // Cleanly shuts down the client
    }
}

/**
 * Defines the application's dependency injection module.
 */
fun appModule() = module {
    single { Config }
    single { get<Config>().httpClient }
    single { get<Config>().apiBaseUrl }
    single { get<Config>().maxRetries }
    single { DistanceCalculator(DistanceCalculatorStrategies.haversine) }
    single { DeliveryFeeCalculator(DeliveryFeeCalculatorStrategies.default) }
    single { HomeAssignmentApiClient(get(), get()) }
    single<DeliveryOrderPriceService> { DeliveryOrderPriceServiceImpl(get(), get(), get()) }
}


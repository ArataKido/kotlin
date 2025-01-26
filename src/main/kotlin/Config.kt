package com.dcop


import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.math.pow

object Config {
    private const val DEFAULT_API_BASE_URL = "https://api.example.com"
    private const val DEFAULT_MAX_RETRIES = 3

    val apiBaseUrl: String = System.getenv("API_BASE_URL") ?: DEFAULT_API_BASE_URL
    val maxRetries: Int = System.getenv("MAX_RETRIES")?.toIntOrNull() ?: DEFAULT_MAX_RETRIES

    val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            install(HttpRequestRetry) {
                maxRetries = this@Config.maxRetries
                retryOnExceptionIf { _, cause ->
                    cause is java.io.IOException
                }
                retryIf { request, response ->
                    response.status.value in 500..599
                }
                delayMillis { retryCount ->
                    (1000L * 2.0.pow(retryCount.toDouble())).toLong().coerceAtMost(10_000L)
                }
                exponentialDelay()
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }
    }

    fun closeHttpClient() {
        httpClient.close()
    }
}

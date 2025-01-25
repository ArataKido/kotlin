package com.dcop

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

data class Config(
    val httpClient: HttpClient,
    val apiBaseUrl: String,
    val maxRetries: Int
)

fun loadConfig(): Config {
    val apiBaseUrl = System.getenv("API_BASE_URL") ?: "https://api.example.com"
    val maxRetries = System.getenv("MAX_RETRIES")?.toIntOrNull() ?: 3

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = maxRetries)
            exponentialDelay()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    return Config(httpClient, apiBaseUrl, maxRetries)
}


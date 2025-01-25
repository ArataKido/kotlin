package com.dcop.models

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponse(
    val message: String,
    val code: Int
)
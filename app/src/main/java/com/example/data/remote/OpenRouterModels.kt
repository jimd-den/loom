package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double = 0.7
)

@JsonClass(generateAdapter = true)
data class OpenRouterChoice(
    val message: OpenRouterMessage?
)

@JsonClass(generateAdapter = true)
data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>? = null,
    val error: OpenRouterError? = null
)

@JsonClass(generateAdapter = true)
data class OpenRouterError(
    val message: String? = null,
    val code: Int? = null
)

@JsonClass(generateAdapter = true)
data class OpenRouterModelItem(
    val id: String,
    val name: String,
    val description: String? = null
)

@JsonClass(generateAdapter = true)
data class OpenRouterModelsResponse(
    val data: List<OpenRouterModelItem>? = null
)

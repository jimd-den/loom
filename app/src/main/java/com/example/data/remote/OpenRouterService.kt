package com.example.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterService {
    @POST("api/v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://ai.studio/build",
        @Header("X-Title") title: String = "Loom App",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse

    @GET("api/v1/models")
    suspend fun getModels(
        @Header("HTTP-Referer") referer: String = "https://ai.studio/build",
        @Header("X-Title") title: String = "Loom App"
    ): OpenRouterModelsResponse
}

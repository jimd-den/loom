package com.example.data.remote

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object OpenRouterClient {
    private const val TAG = "OpenRouterClient"
    private const val BASE_URL = "https://openrouter.ai/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: OpenRouterService = retrofit.create(OpenRouterService::class.java)

    /**
     * Executes a prompt with the chosen model, falling back to a secondary model if needed, 
     * and returns the content string.
     */
    suspend fun generateChat(
        apiKey: String,
        model: String,
        fallbackModel: String,
        systemPrompt: String,
        userPrompt: String
    ): String {
        val cleanToken = if (apiKey.startsWith("Bearer ")) apiKey else "Bearer $apiKey"
        val request = OpenRouterRequest(
            model = model,
            messages = listOf(
                OpenRouterMessage(role = "system", content = systemPrompt),
                OpenRouterMessage(role = "user", content = userPrompt)
            )
        )

        return try {
            val response = service.getCompletion(authorization = cleanToken, request = request)
            response.choices?.firstOrNull()?.message?.content 
                ?: response.error?.message 
                ?: throw Exception("Empty response or unhandled API error")
        } catch (e: Exception) {
            Log.e(TAG, "Primary model failed: ${e.message}. Trying fallback: $fallbackModel", e)
            try {
                // Attempt fallback
                val fallbackRequest = request.copy(model = fallbackModel)
                val fallbackResponse = service.getCompletion(authorization = cleanToken, request = fallbackRequest)
                fallbackResponse.choices?.firstOrNull()?.message?.content 
                    ?: fallbackResponse.error?.message 
                    ?: throw Exception("Fallback response was empty")
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "Fallback model also failed: ${fallbackEx.message}", fallbackEx)
                "Error connecting to OpenRouter. Please review your API key, check active models, and confirm your internet connection (Details: ${e.localizedMessage})"
            }
        }
    }
}

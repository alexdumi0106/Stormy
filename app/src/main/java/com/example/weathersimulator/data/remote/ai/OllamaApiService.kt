package com.example.weathersimulator.data.remote.ai

import retrofit2.http.Body
import retrofit2.http.POST

interface OllamaApiService {
    @POST("generate")
    suspend fun generate(
        @Body body: OllamaRequest
    ): OllamaResponse

    @POST("generate-local")
    suspend fun generateLocal(
        @Body body: OllamaRequest
    ): OllamaResponse

    @POST("ai/sky-observation")
    suspend fun generateSkyObservation(
        @Body body: SkyObservationRequest
    ): OllamaResponse
}

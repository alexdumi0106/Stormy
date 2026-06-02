package com.example.weathersimulator.repository

import com.example.weathersimulator.data.remote.ai.SkyObservationRequest

interface AiRepository {
    suspend fun generate(prompt: String, serverUrl: String): String

    suspend fun generateLocal(prompt: String, serverUrl: String): String
    
    suspend fun generateSkyObservation(request: SkyObservationRequest, serverUrl: String): String
}

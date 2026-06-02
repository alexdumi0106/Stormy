package com.example.weathersimulator.domain.usecase

import com.example.weathersimulator.data.remote.ai.SkyObservationRequest
import com.example.weathersimulator.repository.AiRepository
import javax.inject.Inject

class GenerateAiResponseUseCase @Inject constructor(
    private val repo: AiRepository
) {
    suspend operator fun invoke(prompt: String, serverUrl: String): String =
        repo.generate(prompt, serverUrl)

    suspend fun local(prompt: String, serverUrl: String): String =
        repo.generateLocal(prompt, serverUrl)

    suspend fun skyObservation(request: SkyObservationRequest, serverUrl: String): String =
        repo.generateSkyObservation(request, serverUrl)
}

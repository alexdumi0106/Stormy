package com.example.weathersimulator.data.remote.ai

data class SkyObservationRequest(
    val cloudType: String,
    val rainProbability: Int,
    val stormProbability: Int,
    val photographyScore: Int,
    val bestMoment: String,
    val sunsetScore: Int,
    val sunriseScore: Int,
    val stormScore: Int,
    val dramaticCloudsScore: Int,
    val fogScore: Int,
    val skyRatio: Float,
    val cloudRatio: Float,
    val darkCloudRatio: Float,
    val warmLightRatio: Float,
    val averageBrightness: Float,
    val averageSaturation: Float,
    val contrast: Float
)
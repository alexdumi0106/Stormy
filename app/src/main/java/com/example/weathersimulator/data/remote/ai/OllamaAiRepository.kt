package com.example.weathersimulator.data.remote.ai

import com.example.weathersimulator.repository.AiRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OllamaAiRepository @Inject constructor() : AiRepository {

    override suspend fun generate(prompt: String, serverUrl: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)

        return requestWithLocalFallback(
            api = api,
            localPrompt = prompt
        ) {
            api.generate(OllamaRequest(prompt))
        }
    }

    override suspend fun generateLocal(prompt: String, serverUrl: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)

        return api.generateLocal(OllamaRequest(prompt)).response.requireUsableOllamaResponse()
    }

    override suspend fun generateSkyObservation(
        request: SkyObservationRequest,
        serverUrl: String
    ): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)

        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalSkyObservationPrompt()
        ) {
            api.generateSkyObservation(request)
        }
    }

    override suspend fun generateOutfitRecommendation(
        request: OutfitRecommendationRequest,
        serverUrl: String
    ): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)

        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalOutfitRecommendationPrompt()
        ) {
            api.generateOutfitRecommendation(request)
        }
    }

    override suspend fun generateWeatherSimulation(
        request: WeatherSimulationRequest,
        serverUrl: String
    ): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)

        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalWeatherSimulationPrompt()
        ) {
            api.generateWeatherSimulation(request)
        }
    }

    override suspend fun generateWeatherStory(
        request: WeatherStoryRequest,
        serverUrl: String
    ): String {
        val client = buildClient()
        val api = buildApi(serverUrl, client)
        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalWeatherStoryPrompt()
        ) {
            api.generateWeatherStory(request)
        }
    }

    override suspend fun generateHistoricalDayDescription(
        request: HistoricalDayDescriptionRequest,
        serverUrl: String
    ): String {
        val client = buildClient()
        val api = buildApi(serverUrl, client)
        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalHistoricalDayDescriptionPrompt()
        ) {
            api.generateHistoricalDayDescription(request)
        }
    }

    override suspend fun generateClimateComparison(
        request: ClimateComparisonRequest,
        serverUrl: String
    ): String {
        val client = buildClient()
        val api = buildApi(serverUrl, client)
        return requestWithLocalFallback(
            api = api,
            localPrompt = request.toLocalClimateComparisonPrompt()
        ) {
            api.generateClimateComparison(request)
        }
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(1000, TimeUnit.SECONDS)
            .build()
    }

    private fun buildApi(serverUrl: String, client: OkHttpClient): OllamaApiService {
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApiService::class.java)
    }

    private suspend fun requestWithLocalFallback(
        api: OllamaApiService,
        localPrompt: String,
        primaryRequest: suspend () -> OllamaResponse
    ): String {
        return try {
            primaryRequest().response.requireUsableOllamaResponse()
        } catch (e: OllamaBackendException) {
            if (!e.shouldUseLocalFallback()) {
                throw e
            }

            api.generateLocal(OllamaRequest(localPrompt)).response.requireUsableOllamaResponse()
        } catch (e: HttpException) {
            if (!e.shouldUseLocalFallback()) {
                throw e
            }

            api.generateLocal(OllamaRequest(localPrompt)).response.requireUsableOllamaResponse()
        }
    }

    private fun String.requireUsableOllamaResponse(): String {
        val cleaned = trim()

        if (cleaned.isBlank()) {
            throw OllamaBackendException("Backend-ul local nu a returnat text.")
        }

        val lower = cleaned.lowercase()
        val isBackendError =
            lower.startsWith("backend error:") ||
                lower.startsWith("backend local error:") ||
                lower.contains("llama runner process has terminated") ||
                lower.contains("unable to allocate cpu buffer") ||
                lower.contains("status code: 500") ||
                lower.contains("panic:")

        if (isBackendError) {
            throw OllamaBackendException(cleaned)
        }

        return cleaned
    }

    private fun Throwable.shouldUseLocalFallback(): Boolean {
        val httpCode = (this as? HttpException)?.code()
        val errorBody = (this as? HttpException)
            ?.response()
            ?.errorBody()
            ?.string()
            .orEmpty()
        val lower = "${message.orEmpty()}\n$errorBody".lowercase()
        val mentionsGemini = lower.contains("gemini")
        val isPrimaryBackendError = lower.trimStart().startsWith("backend error:")
        val isTemporaryHttpError = httpCode == 429 ||
            httpCode == 503 ||
            (httpCode == 500 && mentionsGemini)
        val mentionsTemporaryAiProblem =
            lower.contains("unavailable") ||
                lower.contains("high demand") ||
                lower.contains("internal") ||
                lower.contains("rate limit") ||
                lower.contains("resource_exhausted") ||
                lower.contains("quota") ||
                lower.contains("overloaded") ||
                lower.contains("temporar") ||
                lower.contains("429") ||
                lower.contains("500") ||
                lower.contains("503")

        return isTemporaryHttpError ||
            isPrimaryBackendError ||
            (
                mentionsGemini &&
                    mentionsTemporaryAiProblem
                )
    }

    private fun SkyObservationRequest.toLocalSkyObservationPrompt(): String {
        return """
            Esti asistentul AI pentru analiza cerului din aplicatia meteo.
            Raspunde in romana, pe baza indicatorilor calculati local. Explica pe scurt ce se vede pe cer, sansele de ploaie/furtuna si daca momentul este potrivit pentru fotografie.

            Date analiza cer:
            Tip nori: $cloudType
            Probabilitate ploaie: $rainProbability%
            Probabilitate furtuna: $stormProbability%
            Scor fotografie: $photographyScore
            Cel mai bun moment: $bestMoment
            Scor apus: $sunsetScore
            Scor rasarit: $sunriseScore
            Scor furtuna: $stormScore
            Scor nori dramatici: $dramaticCloudsScore
            Scor ceata: $fogScore
            Raport cer: $skyRatio
            Raport nori: $cloudRatio
            Raport nori intunecati: $darkCloudRatio
            Lumina calda: $warmLightRatio
            Luminozitate medie: $averageBrightness
            Saturatie medie: $averageSaturation
            Contrast: $contrast
        """.trimIndent()
    }

    private fun OutfitRecommendationRequest.toLocalOutfitRecommendationPrompt(): String {
        return """
            Esti asistentul AI pentru recomandari vestimentare in functie de vreme.
            Raspunde in romana, practic si concis. Recomanda tinuta potrivita si mentioneaza motivele meteo principale.

            Oras: $cityName
            Temperatura: ${temperature ?: "necunoscuta"} C
            Temperatura resimtita: ${apparentTemperature ?: "necunoscuta"} C
            Umiditate: ${humidity ?: "necunoscuta"}%
            Vant: ${windSpeed ?: "necunoscut"} km/h
            Precipitatii urmatoarele ore: ${precipitationNextHours.joinToString()}
            UV: ${uvIndex ?: "necunoscut"}
            Momentul zilei: $momentOfDay
            Ore urmatoare: ${nextHours.joinToString()}
            Temperaturi urmatoare: ${nextTemperatures.joinToString()}
        """.trimIndent()
    }

    private fun WeatherStoryRequest.toLocalWeatherStoryPrompt(): String {
        return """
            Esti asistentul AI care scrie interpretarea vremii curente.
            Raspunde in romana, in 2-3 fraze naturale. Foloseste doar numere intregi, fara zecimale.

            Temperatura: ${temperature ?: "necunoscuta"} C
            Temperatura resimtita: ${apparentTemperature ?: "necunoscuta"} C
            Umiditate: ${humidity ?: "necunoscuta"}%
            Vant: ${windSpeed ?: "necunoscut"} km/h
            Presiune: ${pressure ?: "necunoscuta"} hPa
            Cod vreme: ${weatherCode ?: "necunoscut"}
            Nebulozitate: ${cloudCover ?: "necunoscuta"}%
            Ore urmatoare: ${nextHours.joinToString()}
            Temperaturi urmatoare: ${nextTemperatures.joinToString()}
            Precipitatii urmatoare: ${nextPrecipitation.joinToString()}
            Vant urmator: ${nextWindSpeed.joinToString()}
            Coduri vreme urmatoare: ${nextWeatherCodes.joinToString()}
            Zile: ${dailyDates.joinToString()}
            Maxime zilnice: ${dailyMaxTemperatures.joinToString()}
            Minime zilnice: ${dailyMinTemperatures.joinToString()}
            Coduri zilnice: ${dailyWeatherCodes.joinToString()}
        """.trimIndent()
    }

    private fun HistoricalDayDescriptionRequest.toLocalHistoricalDayDescriptionPrompt(): String {
        return """
            Esti asistentul AI pentru descrierea unei zile meteo istorice.
            Raspunde in romana, clar si concis, descriind cum a fost vremea si ce a iesit in evidenta.

            Data: $dateLabel
            Temperatura maxima: $maxTemperature
            Temperatura minima: $minTemperature
            Umiditate medie: $averageHumidity
            Presiune medie: $averagePressure
            Rasarit: ${sunrise ?: "necunoscut"}
            Apus: ${sunset ?: "necunoscut"}
            Instantanee orare:
            ${hourlySnapshots.joinToString(separator = "\n") { "${it.time}: ${it.temperature}, cod ${it.weatherCode}, nori ${it.cloudCover}%" }}
        """.trimIndent()
    }

    private fun ClimateComparisonRequest.toLocalClimateComparisonPrompt(): String {
        return """
            Esti asistentul AI pentru comparatii climatice intre doua zile.
            Raspunde in romana, compara diferentele principale de temperatura, umiditate si presiune si mentioneaza care zi pare mai calda, mai rece sau mai stabila.

            Zi selectata:
            ${selectedDay.toLocalClimateSummaryText()}

            Zi de comparatie:
            ${comparisonDay.toLocalClimateSummaryText()}
        """.trimIndent()
    }

    private fun ClimateDaySummaryRequest.toLocalClimateSummaryText(): String {
        return """
            Data: $dateLabel
            Maxima: $maxTemperature
            Minima: $minTemperature
            Umiditate medie: $averageHumidity
            Presiune medie: $averagePressure
        """.trimIndent()
    }

    private fun WeatherSimulationRequest.toLocalWeatherSimulationPrompt(): String {
        return """
            Esti asistentul AI din modulul Simulator meteo al aplicatiei.
            Raspunde in romana, clar si practic, despre fenomene meteo, parametri de simulare si scenarii de vreme.
            Daca utilizatorul intreaba cum sa obtina un fenomen, explica ce schimbari sunt utile pentru temperatura, umiditate, presiune, vant, nori si precipitatii.

            Intrebarea utilizatorului:
            $prompt
        """.trimIndent()
    }
}

class OllamaBackendException(
    message: String
) : Exception(message)

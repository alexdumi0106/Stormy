package com.example.weathersimulator.data.remote.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoArchiveApi {

    @GET("v1/archive")
    suspend fun archive(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,

        @Query("hourly") hourly: String =
            "temperature_2m,relative_humidity_2m,surface_pressure,cloud_cover,wind_speed_10m,wind_gusts_10m,precipitation,rain,weather_code,is_day",

        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset",

        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

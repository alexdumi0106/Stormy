package com.example.weathersimulator.data.remote.ai

data class WeatherStoryRequest(
    val temperature: Int?,
    val apparentTemperature: Int?,
    val humidity: Int?,
    val windSpeed: Int?,
    val pressure: Int?,
    val weatherCode: Int?,
    val cloudCover: Int?,
    val nextHours: List<String>,
    val nextTemperatures: List<Int>,
    val nextPrecipitation: List<Int>,
    val nextWindSpeed: List<Int>,
    val nextWeatherCodes: List<Int>,
    val dailyDates: List<String>,
    val dailyMaxTemperatures: List<Int>,
    val dailyMinTemperatures: List<Int>,
    val dailyWeatherCodes: List<Int>
)

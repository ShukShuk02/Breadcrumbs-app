package com.breadcrumbs.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherSnapshot(
    val summary: String,
    val temperatureC: Double
)

class WeatherRepository {

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherSnapshot? {
        return try {
            val response = api.getCurrentWeather(
                latitude = latitude,
                longitude = longitude
            )
            val current = response.current ?: return null
            val temperatureC = current.temperature2m ?: return null
            val weatherCode = current.weatherCode ?: return null

            WeatherSnapshot(
                summary = weatherCodeToSummary(weatherCode),
                temperatureC = temperatureC
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun weatherCodeToSummary(code: Int): String {
        return when (code) {
            0 -> "Clear sky ☀️"
            1 -> "Mainly clear 🌤️"
            2 -> "Partly cloudy ⛅"
            3 -> "Overcast 🌥️"
            45, 48 -> "Fog ☁️"
            51, 53, 55 -> "Drizzle ☔"
            56, 57 -> "Freezing drizzle 🌦️"
            61, 63, 65 -> "Rain 🌦️"
            66, 67 -> "Freezing rain ❄️"
            71, 73, 75, 77 -> "Snow 🌨️"
            80, 81, 82 -> "Rain showers 🌧️"
            85, 86 -> "Snow showers ☃️"
            95 -> "Thunderstorm 🌩️"
            96, 99 -> "Thunderstorm with hail ⛈️"
            else -> "Unknown weather 🌈"
        }
    }

    interface OpenMeteoApi {
        @GET("v1/forecast")
        suspend fun getCurrentWeather(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("current") current: String = "temperature_2m,weather_code",
            @Query("temperature_unit") temperatureUnit: String = "celsius"
        ): OpenMeteoResponse
    }

    data class OpenMeteoResponse(
        val current: CurrentWeatherDto?
    )

    data class CurrentWeatherDto(
        @com.google.gson.annotations.SerializedName("temperature_2m")
        val temperature2m: Double?,
        @com.google.gson.annotations.SerializedName("weather_code")
        val weatherCode: Int?
    )

    companion object {
        private val api: OpenMeteoApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApi::class.java)
        }
    }
}

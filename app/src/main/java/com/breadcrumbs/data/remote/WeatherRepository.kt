package com.breadcrumbs.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale

data class WeatherSnapshot(
    val summary: String,
    val temperatureC: Double
)

class WeatherRepository {

    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherSnapshot? {
        return withContext(Dispatchers.IO) {
            try {
                val endpoint = String.format(
                    Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,weather_code&temperature_unit=celsius",
                    latitude,
                    longitude
                )

                val response = URL(endpoint).readText()
                val current = JSONObject(response).optJSONObject("current") ?: return@withContext null
                val temperatureC = current.optDouble("temperature_2m", Double.NaN)
                val weatherCode = current.optInt("weather_code", -1)

                if (temperatureC.isNaN()) return@withContext null

                WeatherSnapshot(
                    summary = weatherCodeToSummary(weatherCode),
                    temperatureC = temperatureC
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun weatherCodeToSummary(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            56, 57 -> "Freezing drizzle"
            61, 63, 65 -> "Rain"
            66, 67 -> "Freezing rain"
            71, 73, 75, 77 -> "Snow"
            80, 81, 82 -> "Rain showers"
            85, 86 -> "Snow showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with hail"
            else -> "Unknown weather"
        }
    }
}

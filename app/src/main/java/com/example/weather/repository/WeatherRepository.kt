package com.example.weather.repository

import com.example.weather.api.RetrofitWeatherInstance
import com.example.weather.model.WeatherInfo
import retrofit2.Response

class WeatherRepository {

    suspend fun getWeatherByID(place: String): Response<WeatherInfo> {
        return RetrofitWeatherInstance.api.getWeather(place)
    }
}
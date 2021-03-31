package com.example.weather.api

import com.example.weather.model.WeatherInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface WeatherApi {

    @GET
    suspend fun getWeather(
            @Url url: String
    ): Response<WeatherInfo>
}
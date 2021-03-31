package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.repository.*

class MainViewModelFactory(
    private val repositoryWeather: WeatherRepository,

): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repositoryWeather) as T
    }
}
package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.WeatherInfo
import com.example.weather.repository.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repositoryWeather: WeatherRepository) :ViewModel() {

    var place = "Gliwice"
    var isElder = false   // Elder mode inactive

    val responseWeather: MutableLiveData<Response<WeatherInfo>> = MutableLiveData()

    // Function that sends request for weather data for given place and parses it into a data class if the request was successful
    fun getWeather(place: String) {

        val url = "/data/2.5/weather?q=${place}&APPID=76db8edfe22673209464fa0660fb02b2"

        viewModelScope.launch {
            val response = repositoryWeather.getWeatherByID(url)
            responseWeather.value = response
        }
    }

}
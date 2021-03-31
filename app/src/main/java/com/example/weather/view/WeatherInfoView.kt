package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.model.WeatherInfo
import com.example.weather.repository.WeatherRepository
import com.example.weather.viewmodel.MainViewModel
import com.example.weather.viewmodel.MainViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather_info.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private const val ARG_ELDER1 = "false"
private const val ARG_PLACE = "place"

class WeatherInfoView : Fragment() {

    private var isElderModeEnabled: Boolean? = false
    private var place: String? = ""
    private lateinit var  viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isElderModeEnabled = it.getBoolean(ARG_ELDER1)
            place = it.getString(ARG_PLACE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repository = WeatherRepository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // acquiring api response for entered place
        viewModel.getWeather(place!!)
        viewModel.responseWeather.observe(this, Observer { response ->
            if (response.isSuccessful){
                //Log.d("Response", response.body()?.weather.toString())
                showWeatherInfo(response.body()!!)
            } else {
                //Log.e("Response", response.errorBody().toString())
                Toast.makeText(context, "Sorry, location not found.", Toast.LENGTH_SHORT).show()
            }
        })
        return inflater.inflate(R.layout.fragment_weather_info, container, false)
    }

    // Weather Function
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showWeatherInfo(weatherInfo: WeatherInfo) {

        val temp = weatherInfo.main.temp - 272.15
        val timeZone = if (weatherInfo.timezone / 3600 > 0) "GMT+${weatherInfo.timezone / 3600}" else "GMT${weatherInfo.timezone / 3600}"
        val iconUrl = "https://openweathermap.org/img/wn/${weatherInfo.weather[0].icon.dropLast(1)}d@4x.png"

        // Date and time
        date.text = LocalDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        time.text = LocalDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern("HH:mm"))

        // Weather Icon
        Picasso.with(context).load(iconUrl).into(icon_weather)

        // Temperature and Pressure
        temperature.text = "${"%.0f".format(temp)}°C"
        pressure.text = "${weatherInfo.main.pressure}hPa"

        // Dawn and Dusk times
        dawn_time.text = formatTime(weatherInfo.sys.sunrise)
        dusk_time.text = formatTime(weatherInfo.sys.sunset)

        // Description
        note.text = weatherInfo.weather[0].description.capitalize(Locale.ROOT)

        // Wind Speed and Direction
        wind_speed.text = "${weatherInfo.wind.speed}m/s"
        wind_direction.text = degToDirection(weatherInfo.wind.deg)
        wind_direction_icon.rotation = (270 + weatherInfo.wind.deg).toFloat()

        // that ugly from bellow
        elderMode(isElderModeEnabled!!)
    }

    // Ugly Function for font size adapting
    private fun elderMode(isOn: Boolean) {
        if (isOn)
        {
            date.textSize                   = 36f
            time.textSize                   = 36f

            temperature_label.textSize      = 24f
            pressure_label.textSize         = 24f
            dawn_time_label.textSize        = 24f
            dusk_time_label.textSize        = 24f
            wind_speed_label.textSize       = 24f
            wind_direction_label.textSize   = 24f

            temperature.textSize            = 42f
            pressure.textSize               = 42f
            dawn_time.textSize              = 42f
            dusk_time.textSize              = 42f
            wind_speed.textSize             = 42f
            wind_direction.textSize         = 42f
            note.textSize                   = 30f
        } else {
            date.textSize                   = 24f
            time.textSize                   = 24f

            temperature_label.textSize      = 14f
            pressure_label.textSize         = 14f
            dawn_time_label.textSize        = 14f
            dusk_time_label.textSize        = 14f
            wind_speed_label.textSize       = 14f
            wind_direction_label.textSize   = 14f

            temperature.textSize            = 30f
            pressure.textSize               = 30f
            dawn_time.textSize              = 30f
            dusk_time.textSize              = 30f
            wind_speed.textSize             = 30f
            wind_direction.textSize         = 30f
            note.textSize                   = 20f

        }
    }

    // Function to change unix time to 24h format
    @SuppressLint("SimpleDateFormat")
    private fun formatTime(unix: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm")
        val date = Date(unix * 1000)
        return sdf.format(date)
    }

    // Function that changes degree direction to direction shortcut
    private fun degToDirection(deg: Int): String {
        val directions = arrayOf("N","NNE","NE","ENE","E","ESE", "SE","SSE","S","SSW","SW","WSW", "W","WNW","NW","NNW","N")
        return directions[(deg / 22.5).toInt() % 16]
    }

    companion object {

        @JvmStatic
        fun newInstance(isElder: Boolean, place: String) =
            WeatherInfoView().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_ELDER1, isElder)
                    putString(ARG_PLACE, place)
                }
            }
    }
}
package com.example.weatherapp

import android.app.VoiceInteractor
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.net.URL
import kotlin.math.roundToInt

class weatherforCity : AppCompatActivity() {

    lateinit var url: String
    private var requestQueue: RequestQueue? = null
    lateinit var weatherModel: WeatherModel

    lateinit var tv_city: TextView
    lateinit var tv_temp: TextView
    lateinit var tv_feelsLike: TextView
    lateinit var iv_icon: ImageView
    lateinit var tv_wind: TextView
    lateinit var tv_humidity: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weatherfor_city)
        val toolbar = supportActionBar
        toolbar?.title = "Weather"
        toolbar?.setDisplayHomeAsUpEnabled(true)
        requestQueue = Volley.newRequestQueue(this)

        tv_city = findViewById(R.id.weatherForCity_tv_city)
        tv_temp = findViewById(R.id.weatherForCity_tv_temprature)
        tv_feelsLike = findViewById(R.id.weatherForCity_tv_feelsLike)
        iv_icon = findViewById(R.id.weatherForCity_iv_weatherImage)
        tv_wind = findViewById(R.id.weatherForCity_tv_wind)
        tv_humidity = findViewById(R.id.weatherForCity_tv_humidity)

        fetchAllData()
    }

    private fun fetchAllData() {
        var intent: Intent = intent
        weatherModel = intent.getParcelableExtra<WeatherModel>("weatherObject")!!
        showOntheScreen(weatherModel)

    }

    private fun showOntheScreen(weatherModel: WeatherModel) {
        tv_city.text = ("${weatherModel.cityName}, ${weatherModel.country}")
        tv_temp.text = ("${weatherModel.temperature?.roundToInt()} \u2103")
        tv_feelsLike.text = ("Feels like: ${weatherModel.feelsLike?.roundToInt()} \u2103")
        tv_wind.text = ("Wind: ${weatherModel.wind} m/s")
        tv_humidity.text = ("Humidity: ${weatherModel.humidity}%")
        var imageURL = "https://openweathermap.org/img/wn/${weatherModel.icon}@4x.png"

        Picasso.get().load(imageURL).into(iv_icon)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
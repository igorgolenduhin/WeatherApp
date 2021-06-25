package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    lateinit var city: AutoCompleteTextView
    lateinit var recyclerView: RecyclerView
    private var requestQueue: RequestQueue? = null
    lateinit var adapter: ArrayAdapter<String>
    lateinit var db: FirebaseFirestore
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var weatherModel: WeatherModel
    lateinit var jsonObjectRequest: JsonObjectRequest
    lateinit var WEATHER_KEY: String
    lateinit var HEADER_KEY: String
    lateinit var userWeatherList: ArrayList<WeatherModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        city = findViewById(R.id.mainActivity_et_city)
        recyclerView = findViewById(R.id.mainActivity_rv_allcities)
        requestQueue = Volley.newRequestQueue(this)
        db = FirebaseFirestore.getInstance()
        WEATHER_KEY = getString(R.string.weatherKey)
        HEADER_KEY = getString(R.string.headerKey)
        userWeatherList = ArrayList()

        findCorrectCity()
        setRecyclerView()
    }

    private fun fetchWeather(userCities: ArrayList<String>) {
        if (userCities != null) {
            for (city in userCities) {
                var url =
                    "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$WEATHER_KEY"
                jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        try {
                            //Get lon and lat variables from JSON
                            val coord = response.getJSONObject("coord")
                            val lon = coord.getDouble("lon").toFloat()
                            val lat = coord.getDouble("lat").toFloat()

                            //Get icon
                            val weather = response.getJSONArray("weather")
                            val z = weather.getJSONObject(0)
                            val icon = z.getString("icon")


                            //Get temp, feels_like and humidity
                            val main = response.getJSONObject("main")
                            val temp = main.getDouble("temp").toFloat()
                            val feels_like = main.getDouble("feels_like").toFloat()
                            val humidity = main.getInt("humidity")

                            //Get wind
                            val wind = response.getJSONObject("wind")
                            val speed = wind.getDouble("speed").toFloat()

                            //Get country
                            val sys = response.getJSONObject("sys")
                            val country = sys.getString("country")
                            val city = response.getString("name")

                            weatherModel = WeatherModel(
                                lon, lat, icon, temp, feels_like,
                                humidity, speed, country, city
                            )
                            userWeatherList.add(weatherModel)
                            recyclerView.adapter?.notifyDataSetChanged()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }, { error -> error.printStackTrace() })
                requestQueue?.add(jsonObjectRequest)
                Log.d("eror", userWeatherList.toString())

            }
        }
    }

    private fun makeConnection() {
        db.collection("users").document("${auth.currentUser?.email}").collection("cities")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    var userCities: ArrayList<String> = ArrayList()
                    for (document in result) {
                        userCities.add(document.getString("name").toString())
                    }
                    Log.d("eror", userCities.toString())
                    fetchWeather(userCities)
                    userCities.clear()
                } else {
                    Toast.makeText(
                        this@MainActivity, "No data in Firebase",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            .addOnFailureListener { e -> Log.d("eror", "Firebase error: $e") }
    }


    private fun setAdapterForAutoComplete(findCities: ArrayList<String>) {
        adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, findCities)
        city.setAdapter(adapter)
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = WeatherAdapter(userWeatherList)
    }

    fun updateRecyclerView(view: View) {
        makeConnection()
        setRecyclerView()
        userWeatherList.clear()
    }


    private fun findCorrectCity() {
        city.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var findCities: ArrayList<String> = ArrayList()
                val url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?namePrefix=$p0"

                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                    { response ->
                        try {
                            val data = response.getJSONArray("data")
                            for (i in 0..data.length()) {
                                val objectAPI = data.getJSONObject(i)
                                val city = objectAPI.getString("city")
                                val countryCode = objectAPI.getString("countryCode")

                                val totalCode = "$city,$countryCode"

                                findCities.add(totalCode)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        Log.d("eror", findCities.toString())
                        setAdapterForAutoComplete(findCities)
                    },
                    { error ->
                        Toast.makeText(
                            this@MainActivity,
                            "Something went wrong: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["x-rapidapi-key"] =
                            "$HEADER_KEY"
                        headers["x-rapidapi-host"] = "wft-geo-db.p.rapidapi.com"
                        return headers
                    }
                }
                requestQueue?.add(jsonObjectRequest)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        city.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var cityData: String = city.text.toString()
            val city = hashMapOf(
                "name" to "$cityData"
            )
            db.collection("users").document("${auth.currentUser?.email}").collection("cities")
                .document("$cityData")
                .set(city)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Your city was added",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e -> Log.d("eror", "Error with firebase: $e") }
        }
    }

}
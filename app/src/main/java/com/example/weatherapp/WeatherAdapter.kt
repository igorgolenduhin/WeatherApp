package com.example.weatherapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Deferred

class WeatherAdapter(private val dataSet: ArrayList<WeatherModel>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityName : TextView = view.findViewById(R.id.itemWeatherCity_tv_cityName)
        val cityTemperature : TextView = view.findViewById(R.id.itemWeatherCity_tv_temperature)
        val iconTemperature : ImageView = view.findViewById(R.id.itemWeatherCity_iv_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            cityName.text = dataSet[position].cityName
            cityTemperature.text = "${dataSet[position].temperature.toString()} ℃"
        }
        Picasso.get().load("https://openweathermap.org/img/wn/${dataSet[position].icon}@4x.png").into(holder.iconTemperature)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, weatherforCity::class.java)
            intent.putExtra("weatherObject", dataSet[position])
            holder.itemView.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}
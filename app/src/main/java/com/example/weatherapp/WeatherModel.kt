package com.example.weatherapp

import android.os.Parcel
import android.os.Parcelable

data class WeatherModel(
    var lon: Float?,
    var lat: Float?,
    var icon: String?,
    var temperature: Float?,
    var feelsLike: Float?,
    var humidity: Int?,
    var wind: Float?,
    var country: String?,
    var cityName: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(lon)
        parcel.writeValue(lat)
        parcel.writeString(icon)
        parcel.writeValue(temperature)
        parcel.writeValue(feelsLike)
        parcel.writeValue(humidity)
        parcel.writeValue(wind)
        parcel.writeString(country)
        parcel.writeString(cityName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherModel> {
        override fun createFromParcel(parcel: Parcel): WeatherModel {
            return WeatherModel(parcel)
        }

        override fun newArray(size: Int): Array<WeatherModel?> {
            return arrayOfNulls(size)
        }
    }
}


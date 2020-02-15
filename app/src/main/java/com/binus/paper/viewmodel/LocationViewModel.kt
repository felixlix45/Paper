package com.binus.paper.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binus.paper.api.LocationService
import com.binus.paper.model.LocationRequest
import com.binus.paper.model.LocationResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationViewModel : ViewModel() {

    private lateinit var _location: MutableLiveData<LocationResponse>
    val location: LiveData<LocationResponse> = this._location

    private val baseURL = "https://indoornavigation-260718.appspot.com/api"

    fun getLocation(request: LocationRequest) {

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LocationService::class.java)
        this._location.postValue(api.getLocation(request))

    }

}
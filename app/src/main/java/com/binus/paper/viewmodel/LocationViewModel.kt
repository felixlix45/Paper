package com.binus.paper.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binus.paper.api.LocationService
import com.binus.paper.model.LocationRequest
import com.binus.paper.model.LocationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class LocationViewModel : ViewModel() {

    private val baseURL = "https://indoornavigation-267813.appspot.com/api/"

    private val _response = MutableLiveData<LocationResponse>()
    val response: LiveData<LocationResponse> = this._response

    fun setLocation(request: List<LocationRequest>) {
        Timber.e("Setlocation")
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LocationService::class.java)
        val call = api.getLocation(request)
        call.enqueue(object : Callback<LocationResponse> {
            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Timber.e("Error API : $t")
            }

            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                Timber.e("Response $response, Call $call")
                this@LocationViewModel._response.postValue(response.body())
            }
        })
    }
}
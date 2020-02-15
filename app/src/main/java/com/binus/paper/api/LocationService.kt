package com.binus.paper.api

import com.binus.paper.model.LocationRequest
import com.binus.paper.model.LocationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationService {
    @POST("/position")
    fun getLocation(@Body request: List<LocationRequest>): LocationResponse
}
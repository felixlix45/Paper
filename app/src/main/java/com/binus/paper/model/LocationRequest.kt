package com.binus.paper.model

import com.google.gson.annotations.SerializedName

data class LocationRequest(
    @SerializedName("id")
    val id: String,

    @SerializedName("rssi")
    val rssi: Double
)

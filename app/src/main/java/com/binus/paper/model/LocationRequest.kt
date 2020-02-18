package com.binus.paper.model

import com.google.gson.annotations.SerializedName

data class LocationRequest(
    @SerializedName("id")
    var id: String,

    @SerializedName("rssi")
    var rssi: Double
)

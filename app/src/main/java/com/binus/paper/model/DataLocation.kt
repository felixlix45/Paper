package com.binus.paper.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataLocation(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)
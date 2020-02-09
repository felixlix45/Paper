package com.binus.paper.model

import com.google.gson.annotations.SerializedName

data class Locationresponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("data")
    val data: DataLocation
)
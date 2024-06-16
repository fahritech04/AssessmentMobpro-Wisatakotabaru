package org.d3if3009.mobpro1.model

import com.squareup.moshi.Json

data class Wisatakotabaru(
    val id: String,
    val tempat: String,
    val lokasi: String,
    @Json(name = "image_id") val imageId: String
//    val mine: Int
)

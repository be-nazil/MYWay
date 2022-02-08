package com.nb.myway.ui.model

import com.google.android.gms.maps.model.LatLng

data class DirectionModel(
    val origin: LatLng,
    val destination: LatLng,
    val sensor: String,
    val mode: String,
    val key: String
)

data class Origin(
    val origin: LatLng,
    val dest: LatLng
)
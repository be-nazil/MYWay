package com.nb.myway.ui.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class RouteList(val routeList: List<Route>)

@Parcelize
data class Route(
    val busRouteDetails: String,
    val destinationLat: Double,
    val destinationLong: Double,
    val destinationTime: String,
    val destinationTitle: String,
    val distance: Double,
    val duration: String,
    val fare: Double,
    val medium: String,
    val rideEstimation: String,
    val routeName: String,
    val sourceLat: Double,
    val sourceLong: Double,
    val sourceTime: String,
    val sourceTitle: String,
    val trails: String
): Parcelable


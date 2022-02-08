package com.nb.myway.ui.model


data class RoutesModelItem(
    val routeTitle: String,
    val routes: List<Route>,
    val totalDistance: Double,
    val totalDuration: String,
    val totalFare: Double
)
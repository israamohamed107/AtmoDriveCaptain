package com.israa.atmodrivecaptain.home.data.models

data class TripDetails(
    val cost: String,
    val created_at: String,
    val dropoff_lat: String,
    val dropoff_lng: String,
    val dropoff_location_name: String,
    val estimate_time: String,
    val id: Long,
    val passenger: PassengerDetails,
    val pickup_lat: String,
    val pickup_lng: String,
    val pickup_location_name: String,
    val trip_code: String,
    val trip_color: String,
    val trip_status: String
)
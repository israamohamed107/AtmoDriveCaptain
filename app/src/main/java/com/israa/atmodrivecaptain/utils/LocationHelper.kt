package com.israa.atmodrivecaptain.utils

import com.google.android.gms.maps.model.LatLng

object LocationHelper {
    fun getBearing(begin: LatLng, end: LatLng): Float {
        val dLon = end.longitude - begin.longitude
        val x = Math.sin(Math.toRadians(dLon)) * Math.cos(Math.toRadians(end.latitude))
        val y = (Math.cos(Math.toRadians(begin.latitude)) * Math.sin(Math.toRadians(end.latitude))
                - Math.sin(Math.toRadians(begin.latitude)) * Math.cos(Math.toRadians(end.latitude)) * Math.cos(
            Math.toRadians(dLon)
        ))
        val bearing = Math.toDegrees(Math.atan2(x, y))
        return bearing.toFloat()
    }

}
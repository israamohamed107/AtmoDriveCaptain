package com.israa.atmodrivecaptain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrivecaptain.R
import kotlin.math.abs
import kotlin.math.atan

object MapUtils {
    fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.car)
        return Bitmap.createScaledBitmap(bitmap, 200, 280, true)
    }
    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }

}
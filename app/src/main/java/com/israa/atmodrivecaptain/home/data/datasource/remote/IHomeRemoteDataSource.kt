package com.israa.atmodrivecaptain.home.data.datasource.remote

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.data.models.EndTripData
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.data.models.UpdateAvailabilityResponse

interface IHomeRemoteDataSource {
    suspend fun updateAvailability(captainLat:String,captainLng:String): ResponseState<UpdateAvailabilityResponse>
    suspend fun acceptTrip(tripId:Long,captainLat:String,captainLng:String,captainLocationName:String): ResponseState<TripStatusResponse>

    suspend fun goToPickup(tripId:Long): ResponseState<TripStatusResponse>

    suspend fun arrived(tripId:Long): ResponseState<TripStatusResponse>

    suspend fun cancelTrip(tripId:Long): ResponseState<TripStatusResponse>

    suspend fun startTrip(tripId:Long): ResponseState<TripStatusResponse>

    suspend fun endTrip(tripId:Long,
                        dropOffLat:String,
                        dropOffLng:String,
                        dropOffLocationName:String,
                        distance:Double): ResponseState<EndTripData>

    suspend fun confirmPayment(tripId: Long,amount:Int): ResponseState<TripStatusResponse>
    suspend fun getTripDetails(tripId:Long): ResponseState<TripDetails>
    suspend fun captainOnTrip(): ResponseState<TripDetails>

}
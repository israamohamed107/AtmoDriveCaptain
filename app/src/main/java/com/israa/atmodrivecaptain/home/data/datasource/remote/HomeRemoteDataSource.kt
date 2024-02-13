package com.israa.atmodrivecaptain.home.data.datasource.remote

import android.util.Log
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.data.models.EndTripData
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.data.models.UpdateAvailabilityResponse
import com.israa.atmodrivecaptain.utils.explain
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(private val homeApiServices: IHomeApiServices) : IHomeRemoteDataSource{
    override suspend fun updateAvailability(
        captainLat: String,
        captainLng: String
    ): ResponseState<UpdateAvailabilityResponse> {
        return try {
            val result = homeApiServices.updateAvailability(captainLat, captainLng)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun acceptTrip(
        tripId: Long,
        captainLat: String,
        captainLng: String,
        captainLocationName:String): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.acceptTrip(tripId,captainLat, captainLng, captainLocationName)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }

    }

    override suspend fun goToPickup(tripId: Long): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.goToPickup(tripId)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun arrived(tripId: Long): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.arrived(tripId)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun cancelTrip(tripId: Long): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.cancelTrip(tripId)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun startTrip(tripId: Long): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.startTrip(tripId)
            if(result.status){
                ResponseState.Success(result)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun endTrip(
        tripId: Long,
        dropOffLat: String,
        dropOffLng: String,
        dropOffLocationName: String,
        distance: Double
    ): ResponseState<EndTripData> {
        return try {
            val result = homeApiServices.endTrip(tripId,dropOffLat,dropOffLng,dropOffLocationName,distance)
            if(result.status){
                ResponseState.Success(result.data!!)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun confirmPayment(
        tripId: Long,
        amount: Int
    ): ResponseState<TripStatusResponse> {
        return try {
            val result = homeApiServices.confirmPayment(tripId,amount)
            Log.e("amckm", "confirmPayment: ${result.status}", )
            if(result.status){
                ResponseState.Success(result)
            }
            else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun getTripDetails(tripId: Long): ResponseState<TripDetails> {
        return try {
            val result = homeApiServices.getTripDetails(tripId)
            if(result.status){
                ResponseState.Success(result.data!!)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }

    override suspend fun captainOnTrip(): ResponseState<TripDetails> {
        return try {
            val result = homeApiServices.captainOnTrip()
            if(result.status){
                ResponseState.Success(result.data!!)
            }else{
                ResponseState.Failure(result.message)
            }

        }catch (e:Exception){
            e.explain()
        }
    }
}
package com.israa.atmodrivecaptain.home.data.repo

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.data.datasource.remote.HomeRemoteDataSource
import com.israa.atmodrivecaptain.home.data.models.EndTripData
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.data.models.UpdateAvailabilityResponse
import com.israa.atmodrivecaptain.home.domain.repo.IHomeRepository
import com.israa.atmodrivecaptain.utils.MySharedPreference
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeRemoteDataSource: HomeRemoteDataSource) :
    IHomeRepository {
    override suspend fun updateAvailability(
        captainLat: String,
        captainLng: String
    ): ResponseState<UpdateAvailabilityResponse> {
        return when (val result = homeRemoteDataSource.updateAvailability(captainLat, captainLng)) {
            is ResponseState.Success -> {
                MySharedPreference.putBoolean(
                    MySharedPreference.PreferencesKeys.AVAILABILITY,
                    result.data.available!!
                )
                result
            }

            is ResponseState.Failure -> result

        }
    }

    override suspend fun acceptTrip(
        tripId: Long,
        captainLat: String,
        captainLng: String,
        captainLocationName: String
    ): ResponseState<TripStatusResponse> =
        homeRemoteDataSource.acceptTrip(tripId, captainLat, captainLng, captainLocationName)

    override suspend fun goToPickup(tripId: Long): ResponseState<TripStatusResponse> =
        homeRemoteDataSource.goToPickup(tripId)

    override suspend fun arrived(tripId: Long): ResponseState<TripStatusResponse> =
        homeRemoteDataSource.arrived(tripId)

    override suspend fun cancelTrip(tripId: Long): ResponseState<TripStatusResponse> =
        homeRemoteDataSource.cancelTrip(tripId)

    override suspend fun startTrip(tripId: Long): ResponseState<TripStatusResponse> =
        homeRemoteDataSource.startTrip(tripId)

    override suspend fun endTrip(
        tripId: Long,
        dropOffLat: String,
        dropOffLng: String,
        dropOffLocationName: String,
        distance: Double
    ): ResponseState<EndTripData> =
        homeRemoteDataSource.endTrip(tripId, dropOffLat, dropOffLng, dropOffLocationName, distance)

    override suspend fun confirmPayment(
        tripId: Long,
        amount: Int
    ): ResponseState<TripStatusResponse> = homeRemoteDataSource.confirmPayment(tripId, amount)

    override suspend fun getTripDetails(tripId: Long): ResponseState<TripDetails> =
        homeRemoteDataSource.getTripDetails(tripId)

    override suspend fun captainOnTrip(): ResponseState<TripDetails> = homeRemoteDataSource.captainOnTrip()
}
package com.israa.atmodrivecaptain.home.domain.usecases

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.data.models.EndTripData
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.data.models.UpdateAvailabilityResponse
import com.israa.atmodrivecaptain.home.domain.repo.IHomeRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val iHomeRepository: IHomeRepository) :
    IHomeUseCase {
    override suspend fun updateAvailability(
        captainLat: String,
        captainLng: String
    ): ResponseState<UpdateAvailabilityResponse> =
        iHomeRepository.updateAvailability(captainLat, captainLng)

    override suspend fun acceptTrip(
        tripId: Long,
        captainLat: String,
        captainLng: String,
        captainLocationName: String
    ): ResponseState<TripStatusResponse> =
        iHomeRepository.acceptTrip(tripId, captainLat, captainLng, captainLocationName)

    override suspend fun goToPickup(tripId: Long): ResponseState<TripStatusResponse> =
        iHomeRepository.goToPickup(tripId)

    override suspend fun arrived(tripId: Long): ResponseState<TripStatusResponse> =
        iHomeRepository.arrived(tripId)

    override suspend fun cancelTrip(tripId: Long): ResponseState<TripStatusResponse> =
        iHomeRepository.cancelTrip(tripId)

    override suspend fun startTrip(tripId: Long): ResponseState<TripStatusResponse> =
        iHomeRepository.startTrip(tripId)
    override suspend fun endTrip(
        tripId: Long,
        dropOffLat: String,
        dropOffLng: String,
        dropOffLocationName: String,
        distance: Double
    ): ResponseState<EndTripData> =
        iHomeRepository.endTrip(tripId,dropOffLat, dropOffLng, dropOffLocationName, distance)

    override suspend fun confirmPayment(
        tripId: Long,
        amount: Int
    ): ResponseState<TripStatusResponse>  =
        iHomeRepository.confirmPayment(tripId,amount)

    override suspend fun getTripDetails(tripId: Long): ResponseState<TripDetails> =
        iHomeRepository.getTripDetails(tripId)

    override suspend fun captainOnTrip(): ResponseState<TripDetails> = iHomeRepository.captainOnTrip()

}
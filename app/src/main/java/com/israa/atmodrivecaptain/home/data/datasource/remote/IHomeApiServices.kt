package com.israa.atmodrivecaptain.home.data.datasource.remote

import com.israa.atmodrivecaptain.home.data.models.EndTripResponse
import com.israa.atmodrivecaptain.home.data.models.TripDetailsResponse
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.data.models.UpdateAvailabilityResponse
import com.israa.atmodrivecaptain.utils.ACCEPT_TRIP
import com.israa.atmodrivecaptain.utils.ARRIVED_TRIP
import com.israa.atmodrivecaptain.utils.CANCEL_TRIP
import com.israa.atmodrivecaptain.utils.CAPTAIN_ON_TRIP
import com.israa.atmodrivecaptain.utils.CONFIRM_PAYMENT
import com.israa.atmodrivecaptain.utils.END_TRIP
import com.israa.atmodrivecaptain.utils.PASSENGER_DETAILS
import com.israa.atmodrivecaptain.utils.PICK_TRIP
import com.israa.atmodrivecaptain.utils.START_TRIP
import com.israa.atmodrivecaptain.utils.UPDATE_AVAILABILITY
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IHomeApiServices {

    @POST(UPDATE_AVAILABILITY)
    @FormUrlEncoded
     suspend fun updateAvailability(@Field("captain_lat") captainLat:String,
                            @Field("captain_lng") captainLng:String): UpdateAvailabilityResponse

    @POST(ACCEPT_TRIP)
    @FormUrlEncoded
    suspend fun acceptTrip(@Field("trip_id") tripId:Long,
                           @Field("captain_lat") captainLat:String,
                           @Field("captain_lng") captainLng:String,
                           @Field("captain_location_name") captainLocationName:String): TripStatusResponse

    @POST(PICK_TRIP)
    @FormUrlEncoded
    suspend fun goToPickup(@Field("trip_id") tripId:Long): TripStatusResponse

    @POST(ARRIVED_TRIP)
    @FormUrlEncoded
    suspend fun arrived(@Field("trip_id") tripId:Long): TripStatusResponse

    @POST(CANCEL_TRIP)
    @FormUrlEncoded
    suspend fun cancelTrip(@Field("trip_id") tripId:Long): TripStatusResponse
    @POST(START_TRIP)
    @FormUrlEncoded
    suspend fun startTrip(@Field("trip_id") tripId:Long): TripStatusResponse

    @POST(END_TRIP)
    @FormUrlEncoded
    suspend fun endTrip(@Field("trip_id") tripId:Long,
                          @Field("dropoff_lat") dropOffLat:String,
                          @Field("dropoff_lng") dropOffLng:String,
                        @Field("dropoff_location_name") dropOffLocationName:String,
                        @Field("distance") distance:Double): EndTripResponse

    @POST(PASSENGER_DETAILS)
    @FormUrlEncoded
    suspend fun getTripDetails(@Field("trip_id") tripId:Long):TripDetailsResponse

    @POST(CONFIRM_PAYMENT)
    @FormUrlEncoded
    suspend fun confirmPayment(@Field("trip_id") tripId:Long,
                               @Field("amount") amount:Int): TripStatusResponse

    @GET(CAPTAIN_ON_TRIP)
    suspend fun captainOnTrip(): TripDetailsResponse

}
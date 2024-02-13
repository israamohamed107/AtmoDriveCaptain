package com.israa.atmodrivecaptain.home.presentation.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.domain.usecases.IHomeUseCase
import com.israa.atmodrivecaptain.utils.MySharedPreference
import com.israa.atmodrivecaptain.utils.NEW
import com.israa.atmodrivecaptain.utils.ONLINE_CAPTAINS
import com.israa.atmodrivecaptain.utils.STATUS
import com.israa.atmodrivecaptain.utils.TRIPS
import com.israa.atmodrivecaptain.utils.TRIP_ID
import com.israa.atmodrivecaptain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: IHomeUseCase,
    private val ref: DatabaseReference
) : ViewModel() {

    private var _isCaptainAvailable: MutableLiveData<UiState> = MutableLiveData<UiState>()
    val isCaptainAvailable: LiveData<UiState> get() = _isCaptainAvailable


    private var _tripId: MutableLiveData<UiState?> = MutableLiveData<UiState?>(null)
    val tripId: LiveData<UiState?> = _tripId

    private var _tripStatus: MutableLiveData<UiState?> = MutableLiveData<UiState?>()
    val tripStatus: LiveData<UiState?> get() = _tripStatus

    private var _confirmPayment: MutableLiveData<UiState?> = MutableLiveData<UiState?>()
    val confirmPayment: LiveData<UiState?> get() = _confirmPayment

    private var _tripRealCost: MutableLiveData<String?> = MutableLiveData<String?>(null)
    val tripRealCost: LiveData<String?> get() = _tripRealCost


    private var _tripDetails: MutableLiveData<UiState?> = MutableLiveData<UiState?>(null)
    val tripDetails: LiveData<UiState?> get() = _tripDetails

    private var _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation


    private lateinit var tripIdValueEventListener: ValueEventListener
    private lateinit var tripStatusValueEventListener: ValueEventListener

    private var _counter = MutableLiveData("")
    val counter: LiveData<String> get() = _counter

    private lateinit var timer: CountDownTimer


    fun setCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
    }

    fun resetTripStatus(){
        _tripStatus.value = UiState.Success("")
    }
    fun resetTripDetails(){
        _tripDetails.value = null
    }
    fun getAddress(latLng: LatLng?,context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var address: Address?
        var addressText = ""

        try {
            val addresses: List<Address>? =
                latLng?.let {
                    geocoder.getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )
                }

            addresses?.let {
                if (addresses.isNotEmpty()) {
                    address = addresses[0]
                    addressText = address!!.getAddressLine(0)
                }
            }
        } catch (e: Exception) {
            addressText = "Not Found"
        }

        return addressText
    }

    fun updateAvailability(latLng: LatLng) {
        _isCaptainAvailable.value = UiState.Loading
        viewModelScope.launch {
            when (val res = homeUseCase.updateAvailability(
                latLng.latitude.toString(),
                latLng.longitude.toString()
            )) {
                is ResponseState.Success -> _isCaptainAvailable.postValue(UiState.Success(res.data.available!!))
                is ResponseState.Failure -> _isCaptainAvailable.postValue(UiState.Failure(res.error))
                else -> {}
            }

        }
    }

    fun observeOnTripId() {
        viewModelScope.launch {
            tripIdValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var tripId: Long?
                    if (snapshot.value != null) {
                        tripId = snapshot.getValue(Long::class.java)
                        _tripId.postValue(UiState.Success(tripId!!))

                    }else {
                        _tripId.postValue(null)
                        _tripDetails.postValue(null)
                        _tripRealCost.postValue(null)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    _tripId.postValue(UiState.Failure(error.message))
                }
            }
            ref.child(ONLINE_CAPTAINS)
                .child(MySharedPreference.getString(MySharedPreference.PreferencesKeys.ID)!!)
                .child(TRIP_ID).addValueEventListener(tripIdValueEventListener)
        }
    }

    fun removeObserverOnTripId() {
        viewModelScope.launch {
            ref.child(ONLINE_CAPTAINS)
                .child(MySharedPreference.getString(MySharedPreference.PreferencesKeys.ID)!!)
                .child(TRIP_ID).removeEventListener(tripIdValueEventListener)
        }
    }

    fun observeOnTripStatus(tripId: String) {
            _tripStatus.postValue(UiState.Loading)
            tripStatusValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        _tripStatus.postValue(UiState.Success(snapshot.getValue(String::class.java)!!))
                    }
                    else
                        _tripStatus.postValue(UiState.Success(NEW))
//                    else if (tripId == "0"){
//                        _tripStatus.postValue(UiState.Success(""))
//                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    _tripStatus.postValue(UiState.Failure(error.message))
                }
            }

            ref.child(TRIPS).child(tripId)
                .child(STATUS).addValueEventListener(tripStatusValueEventListener)



    }

    fun removeObserverOnTripStatus(tripId: Long) {
        viewModelScope.launch {
            ref.child(TRIPS)
                .child(tripId.toString())
                .child(STATUS).removeEventListener(tripStatusValueEventListener)
        }
    }

    fun startCounter() {

        timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var seconds = "${millisUntilFinished / 1000}"
                if (seconds.toInt() < 10)
                    seconds = "0$seconds"

                _counter.value = seconds

            }

            override fun onFinish() {
                rejectTrip()
                _counter.value = ""

            }
        }
        timer.start()

    }

    fun stopCounter() {
        timer.cancel()
    }





    fun  logout(){
        MySharedPreference.clear()
    }
    fun acceptTrip(tripId: Long,latLng:LatLng, context: Context) {

        viewModelScope.launch {
            _tripStatus.postValue(UiState.Loading)
            when (val result = homeUseCase.acceptTrip(
                tripId,
                latLng.latitude.toString(),
                latLng.longitude.toString(),
                getAddress(latLng,context)
            )) {
                is ResponseState.Failure ->{
                    _tripStatus.postValue(UiState.Failure(result.error))
                }
                is ResponseState.Success -> {
                    stopCounter()
                }
                else -> {}
            }

        }
    }

    fun rejectTrip() {
        viewModelScope.launch {
            ref.child(ONLINE_CAPTAINS)
                .child(MySharedPreference.getString(MySharedPreference.PreferencesKeys.ID)!!)
                .updateChildren(hashMapOf<String, Any>(TRIP_ID to 0)).addOnSuccessListener {
//                    _tripStatus.postValue(UiState.Success("Rejected"))
//                    _tripId.postValue(UiState.Success(0L))
                    stopCounter()
//                    _tripStatus.postValue(UiState.Success(""))

                }.addOnSuccessListener {
                    stopCounter()
                }
                .addOnFailureListener { e ->
                    _tripStatus.postValue(UiState.Failure(e.message!!))

                }
        }
    }


    fun goToPickup(tripId: Long) {
        _tripStatus.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.goToPickup(tripId)) {
                is ResponseState.Failure -> _tripStatus.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {}
                else -> {}
            }

        }
    }

    fun cancelTrip(tripId: Long) {
        _tripStatus.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.cancelTrip(tripId)) {
                is ResponseState.Failure -> _tripStatus.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {
                    //_tripStatus.postValue(UiState.Success("Cancelled"))


                }

                else -> {}
            }

        }
    }


    fun arrived(tripId: Long) {
        _tripStatus.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.arrived(tripId)) {
                is ResponseState.Failure -> _tripStatus.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {}
                else -> {}
            }

        }
    }

    fun startTrip(tripId: Long) {
        _tripStatus.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.startTrip(tripId)) {
                is ResponseState.Failure -> _tripStatus.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {}
                else -> {}
            }
        }
    }

    fun resetTripCost(){
        _tripRealCost.postValue(null)

    }

    fun endTrip(tripId: Long, latLng: LatLng, context: Context) {
        _tripStatus.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.endTrip(
                tripId,
                latLng.latitude.toString(),
                latLng.longitude.toString(),
                getAddress(latLng,context),
                5000.5
            )) {
                is ResponseState.Failure -> _tripStatus.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {
                    _tripRealCost.postValue(result.data.cost.toString())
                }

                else -> {}
            }

        }
    }

    fun confirmPayment(tripId: Long, amount: Int) {
        viewModelScope.launch {
            _confirmPayment.postValue(UiState.Loading)
            when (val result = homeUseCase.confirmPayment(tripId, amount)) {
                is ResponseState.Failure -> _confirmPayment.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {
                    _confirmPayment.postValue(UiState.Success(result.data))
                }
                else -> {}
            }
        }
    }

     fun getTripDetails(tripId: Long) {
        _tripDetails.postValue(UiState.Loading)
        viewModelScope.launch(Dispatchers.IO){
            when (val result = homeUseCase.getTripDetails(tripId)) {
                is ResponseState.Failure -> {
                    _tripDetails.postValue(UiState.Failure(result.error))
                }
                is ResponseState.Success -> {
                    _tripDetails.postValue(UiState.Success(result.data))
                }

                else -> {}
            }
        }
    }

    fun captainOnTrip() {
        _tripDetails.value = UiState.Loading
        viewModelScope.launch {
            when (val result = homeUseCase.captainOnTrip()) {
                is ResponseState.Failure -> _tripDetails.postValue(UiState.Failure(result.error))
                is ResponseState.Success -> {

                    _tripRealCost.postValue(result.data.cost)
//                    _tripId.postValue(UiState.Success(result.data.id))

                    if (_tripDetails.value == null)
                        _tripDetails.postValue(UiState.Success(result.data))
                }

                else -> {}
            }
        }
    }
}
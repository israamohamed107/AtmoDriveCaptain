package com.israa.atmodrivecaptain.home.presentation.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.home.domain.usecases.HomeUseCase
import com.israa.atmodrivecaptain.utils.LAT
import com.israa.atmodrivecaptain.utils.LNG
import com.israa.atmodrivecaptain.utils.SUCCESS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class UpdateAvailabilityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val workerParams: WorkerParameters
):CoroutineWorker(context,workerParams) {
    @Inject
    lateinit var homeUseCase: HomeUseCase

    override suspend fun doWork(): Result {
        val captainLat = workerParams.inputData.getString(LAT)!!
        val captainLng = workerParams.inputData.getString(LNG)!!
        return withContext(Dispatchers.IO){
            when(val response = homeUseCase.updateAvailability(captainLat,captainLng)){
                is ResponseState.Success -> {
                    Log.e("UpdateAvailabilityWorker", "doWork: $SUCCESS", )

                    Result.success()
                }
                is ResponseState.Failure -> {
                    Log.e("UpdateAvailabilityWorker", "doWork: ${response.error}", )
                    Result.retry()
                }
                else -> {
                    Result.failure()
                }
            }

        }
    }
}
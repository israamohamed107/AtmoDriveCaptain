package com.israa.atmodrivecaptain.home.presentation.di

import com.israa.atmodrivecaptain.home.data.datasource.remote.HomeRemoteDataSource
import com.israa.atmodrivecaptain.home.data.datasource.remote.IHomeApiServices
import com.israa.atmodrivecaptain.home.data.datasource.remote.IHomeRemoteDataSource
import com.israa.atmodrivecaptain.home.data.repo.HomeRepository
import com.israa.atmodrivecaptain.home.domain.repo.IHomeRepository
import com.israa.atmodrivecaptain.home.domain.usecases.HomeUseCase
import com.israa.atmodrivecaptain.home.domain.usecases.IHomeUseCase
import com.israa.atmodrivecaptain.home.presentation.services.LocationClient
import com.israa.atmodrivecaptain.home.presentation.services.LocationClientImpl
import com.israa.atmodrivecaptain.utils.CAPTAIN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    fun getHomeApiService(@Named(CAPTAIN) retrofit: Retrofit): IHomeApiServices = retrofit.create(
        IHomeApiServices::class.java)

    @Provides
    fun getHomeRemoteDataSource(homeRemoteDataSource: HomeRemoteDataSource): IHomeRemoteDataSource = homeRemoteDataSource

    @Provides
    fun getHomeUseCase(homeUseCase: HomeUseCase): IHomeUseCase = homeUseCase

    @Provides
    fun getHomeRepo(homeRepository: HomeRepository): IHomeRepository = homeRepository

    @Provides
    fun getLocation(locationClientImpl: LocationClientImpl):LocationClient = locationClientImpl

}
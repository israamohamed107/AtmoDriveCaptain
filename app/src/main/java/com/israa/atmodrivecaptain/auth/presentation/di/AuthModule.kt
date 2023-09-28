package com.israa.atmodrivecaptain.auth.presentation.di

import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.RemoteDataSource
import com.israa.atmodrive.auth.data.repo.AuthRepo
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import com.israa.atmodrivecaptain.auth.data.datasource.remote.ImageApiService
import com.israa.atmodrivecaptain.auth.domain.repo.IAuthRepo
import com.israa.atmodrivecaptain.utils.CAPTAIN
import com.israa.atmodrivecaptain.utils.IMAGE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    fun getApiServices(@Named(CAPTAIN) retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)


    @Provides
    fun getApiServicesImg(@Named(IMAGE) retrofit: Retrofit): ImageApiService = retrofit.create(
        ImageApiService::class.java)

    @Provides
    @Singleton
    fun getIRepo(remoteDataSource: RemoteDataSource): IAuthRepo = AuthRepo(remoteDataSource)

    @Provides
    @Singleton
    fun getAuthUseCase(repo: IAuthRepo): IAuthUseCase = AuthUseCase(repo)


}
package com.israa.atmodrivecaptain.auth.presentation.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.RemoteDataSource
import com.israa.atmodrive.auth.data.repo.AuthRepo
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import com.israa.atmodrivecaptain.auth.data.datasource.remote.ImageApiService
import com.israa.atmodrivecaptain.auth.domain.repo.IAuthRepo
import com.israa.atmodrivecaptain.home.data.datasource.remote.IHomeApiServices
import com.israa.atmodrivecaptain.utils.BASE_URL_CAPTAIN
import com.israa.atmodrivecaptain.utils.BASE_URL_IMAGE
import com.israa.atmodrivecaptain.utils.CAPTAIN
import com.israa.atmodrivecaptain.utils.IMAGE
import com.israa.atmodrivecaptain.utils.MySharedPreference
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
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(150, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .callTimeout(50, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(object : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url
                val url = originalUrl.newBuilder().build()
//
                val requestBuilder = originalRequest.newBuilder().url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization", "Bearer ${MySharedPreference.getString(MySharedPreference.PreferencesKeys.REMEMBER_TOKEN)}"
                    )
                val request = requestBuilder.build()
                val response = chain.proceed(request) // this fun make the request
                response.code//response status code
                return response
            }
        })
        .build()


    @Named(CAPTAIN)
    @Provides
    fun getRetrofitForCaptain(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL_CAPTAIN)
        .build()

    @Named(IMAGE)
    @Provides
    fun getRetrofitForImage(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL_IMAGE)
        .build()

    @Provides
    fun getDatabaseRef():DatabaseReference = FirebaseDatabase.getInstance().reference

}
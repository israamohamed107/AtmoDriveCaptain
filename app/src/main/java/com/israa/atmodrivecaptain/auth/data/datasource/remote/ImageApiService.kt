package com.israa.atmodrivecaptain.auth.data.datasource.remote

import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.utils.RESISTER_CAPTAIN
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ImageApiService {

    @Multipart
    @POST("upload-files")
    suspend fun uploadImage(
        @Part part: MultipartBody.Part,
        @Part("path") path: RequestBody
    ):UploadImageResponse


    @HTTP(method = "DELETE", path = "delete-uploaded-files", hasBody = true)
        suspend fun deleteImage(
            @Body deleteModel: DeleteModel
        ):DeleteImageResponse

}
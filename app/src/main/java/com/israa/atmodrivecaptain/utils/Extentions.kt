package com.israa.atmodrivecaptain.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException

fun Exception.explain():ResponseState.Failure{
     return when (this) {
        is IOException -> {
            ResponseState.Failure("There is no internet connection")
        }

        is HttpException -> {
            val code = this.code()
            ResponseState.Failure("HttpException  code $code")
        }
        else -> {
            ResponseState.Failure(this.localizedMessage)
        }
    }

}

suspend fun Fragment.decodeFile(filePath: String?): Bitmap? {
    val o = BitmapFactory.Options()
    o.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, o)

// The new size we want to scale to
    val REQUIRED_SIZE = 1024

// Find the correct scale value. It should be the power of 2.
    var width_tmp = o.outWidth
    var height_tmp = o.outHeight
    var scale = 1
    while (true) {
        if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) break
        width_tmp /= 2
        height_tmp /= 2
        scale *= 2
    }

// Decode with inSampleSize
    val o2 = BitmapFactory.Options()
    o2.inSampleSize = scale
    var image = BitmapFactory.decodeFile(filePath, o2)
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath!!)
        val exifOrientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        var rotate = 0f
        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270f
        }
        if (rotate != 0f) {
            val w = image.width
            val h = image.height

            // Setting pre rotate
            val mtx = Matrix()
            mtx.preRotate(rotate)

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false)
        }
    } catch (e: IOException) {
        return null
    }
    return image.copy(Bitmap.Config.ARGB_8888, true)
}


 suspend fun Fragment.uploadImage(image: Uri?) : Pair<MultipartBody.Part,RequestBody>{

    val a = decodeFile(image?.path)
        val baos = ByteArrayOutputStream()
        a?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()

        val requestFile =
            imageBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, imageBytes.size)
        val body =
            MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

        val name: RequestBody = CAPTAIN
            .toRequestBody("text/plain".toMediaTypeOrNull())

    return Pair(body,name)
}

 fun Fragment.pickImage(requestCode: Int) {
    ImagePicker.Companion.with(this)
        .crop()                    //Crop image(Optional), Check Customization for more option
        .compress(1024)            //Final image size will be less than 1 MB(Optional)
        .maxResultSize(
            1080,
            1080
        )    //Final image resolution will be less than 1080 x 1080(Optional)
        .start(requestCode)

}

val <T> T.exhaustive: T
    get() = this
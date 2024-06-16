package org.d3if3009.mobpro1.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3009.mobpro1.model.Wisatakotabaru
import org.d3if3009.mobpro1.network.ApiStatus
import org.d3if3009.mobpro1.network.WisatakotabaruApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Wisatakotabaru>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

//    init {
//        retrieveData()
//    }

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = WisatakotabaruApi.service.getWisatakotabaru(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }


    fun saveData(userId: String, tempat: String, lokasi: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = WisatakotabaruApi.service.postWisatakotabaru(
                    userId,
                    tempat.toRequestBody("text/plain".toMediaTypeOrNull()),
                    lokasi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, WisatakotabaruId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = WisatakotabaruApi.service.deleteWisatakotabaru(userId, WisatakotabaruId)
                if (response.status == "success") {
                    Log.d("MainViewModel", "Image deleted successfully: $WisatakotabaruId")
                    retrieveData(userId) // Refresh data after deletion
                } else {
                    Log.d("MainViewModel", "Failed to delete the image: ${response.message}")
                    errorMessage.value = "Failed to delete the image: ${response.message}"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

    fun clearMessage() { errorMessage.value = null }
}
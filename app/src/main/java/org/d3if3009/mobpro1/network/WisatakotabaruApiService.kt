package org.d3if3009.mobpro1.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3009.mobpro1.model.Wisatakotabaru
import org.d3if3009.mobpro1.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://wisatakotabaru-api-test.000webhostapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WisatakotabaruApiService {
    @GET("api/wisatakotabaru.php")
    suspend fun getWisatakotabaru(
        @Header("authorization") userId: String
    ): List<Wisatakotabaru>

    @Multipart
    @POST("api/wisatakotabaru.php")
    suspend fun postWisatakotabaru(
        @Header("authorization") userId: String,
        @Part("tempat") tempat: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part image: MultipartBody.Part
//        @Part("mine") mine: RequestBody
    ): OpStatus

    @DELETE("api/deleteWisatakotabaru.php")
    suspend fun deleteWisatakotabaru(
        @Header("Authorization") userId: String,
        @Query("id") WisatakotabaruId: String
    ) : OpStatus
}

object WisatakotabaruApi {
    val service: WisatakotabaruApiService by lazy {
        retrofit.create(WisatakotabaruApiService::class.java)
    }

    fun getWisatakotabaruUrl(imageId: String): String {
//        return "$BASE_URL$imageId.jpg"
        return "${BASE_URL}api/image.php?id=$imageId"
    }
}

enum class ApiStatus{ LOADING, SUCCESS, FAILED }
package com.example.dampingi.`interface`

import com.example.dampingi.models.PredictionResult
import com.example.dampingi.models.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictionService {
    @Multipart
    @POST("detections")
    fun predict(
        @Part() image: MultipartBody.Part
    ) : Call<PredictionResult>
}
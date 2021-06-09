package com.example.dampingi.repositories

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.example.dampingi.*
import com.example.dampingi.`interface`.PredictionService
import com.example.dampingi.models.PredictionResult
import com.example.dampingi.utils.Constant.BASE_URL
import com.example.dampingi.utils.RestrictedSocketFactory
import com.example.dampingi.utils.toByteArr
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class PredictionRepositories {
    companion object{

        val predictionRes : MutableLiveData<PredictionResult> = MutableLiveData()



        private fun predictionRepositories(): PredictionService{
            val predict = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return predict.create(PredictionService::class.java)
        }

        @SuppressLint("UnsafeOptInUsageError")
        fun onGetPrediction(imageRes : ImageProxy) {
            Log.e("tes",imageRes.toString())
            val file = imageRes.image!!.toByteArr()
            val body = RequestBody.create(MediaType.parse("image/*"),file)
            val part = MultipartBody.Part.createFormData("images","images",body)
            predictionRepositories().predict(part).enqueue(object : Callback<PredictionResult>{
                override fun onResponse(
                    call: Call<PredictionResult>,
                    response: retrofit2.Response<PredictionResult>
                ) {
                    Log.e("s",response.body().toString())
                    predictionRes.postValue(response.body())
                }
                override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                    predictionRes.postValue(null)
                    Log.e("err",t.message.toString())
                }
            })
        }

        fun getPredictionData(): MutableLiveData<PredictionResult> = predictionRes

    }
}
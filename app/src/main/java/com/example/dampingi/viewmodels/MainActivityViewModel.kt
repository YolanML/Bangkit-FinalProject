package com.example.dampingi.viewmodels

import android.app.Application
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dampingi.models.PredictionResult
import com.example.dampingi.models.Response
import com.example.dampingi.repositories.PredictionRepositories

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    private var predictionResult = MutableLiveData<PredictionResult>()

    init {
        predictionResult = PredictionRepositories.getPredictionData()
    }

    fun uploadPrediction(image: ImageProxy) {
        PredictionRepositories.onGetPrediction(image)
    }

    fun onGetData():LiveData<PredictionResult> =  predictionResult

}
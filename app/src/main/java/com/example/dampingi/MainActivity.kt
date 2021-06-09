package com.example.dampingi

import android.content.pm.PackageManager
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dampingi.databinding.ActivityMainBinding
import com.example.dampingi.utils.Constant.REQUEST_CODE_PERMISSIONS
import com.example.dampingi.utils.Constant.REQUIRED_PERMISSIONS
import com.example.dampingi.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var image: ImageProxy
    private lateinit var oldData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        oldData = ""
        initTTS()
        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainViewModel.onGetData().observe(this, Observer {
            data ->
            if (data == null){
                binding.focusRect.setColorFilter(ContextCompat.getColor(this,R.color.design_default_color_error),PorterDuff.Mode.SRC_IN)
            }
            if (data != null && data.response[0].detections.isNotEmpty()){
                val predict = data.response.first().detections.first().`class`
                if(oldData != predict){
                    mTextToSpeech.speak(predict,TextToSpeech.QUEUE_FLUSH,null,null)
                    binding.focusRect.setColorFilter(ContextCompat.getColor(this,R.color.primaryDarkColor),PorterDuff.Mode.SRC_IN)
                }
            }
            image.close()
        })
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initTTS() {
        mTextToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS){
                mTextToSpeech.language = Locale.ENGLISH

            }
        })
    }

    private fun takePhoto() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor,ImageAnalysis.Analyzer {
                        dat ->
                        image = dat
                        Log.e("img",image.toString())
                        mainViewModel.uploadPrediction(image)
                    })
                }
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
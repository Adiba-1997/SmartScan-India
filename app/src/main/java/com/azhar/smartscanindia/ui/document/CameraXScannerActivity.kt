package com.azhar.smartscanindia.ui.document

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.azhar.smartscanindia.databinding.ActivityCameraScannerBinding
import com.azhar.smartscanindia.utils.FileUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnCapture.setOnClickListener { capture() }
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().setTargetRotation(binding.previewView.display.rotation).build()
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            provider.unbindAll()
            provider.bindToLifecycle(this, selector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capture() {
        val capture = imageCapture ?: return
        val outputFile = FileUtils.newImageFile(this, "DOC")
        val options = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        capture.takePicture(options, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val data = intent.apply { data = Uri.fromFile(outputFile) }
                setResult(RESULT_OK, data)
                finish()
            }

            override fun onError(exception: ImageCaptureException) {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}

package com.example.delivery.presentation.review.camera

import android.Manifest.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.example.delivery.R
import com.example.delivery.databinding.ActivityCameraBinding
import com.example.delivery.extensions.load
import com.example.delivery.presentation.review.camera.preview.ImagePreviewListActivity
import com.example.delivery.presentation.review.gallery.GalleryActivity
import com.example.delivery.util.path.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private lateinit var cameraExecutor: ExecutorService

    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }

    private lateinit var imageCapture: ImageCapture

    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(this)
    }

    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    // Camera Config
    private var displayId: Int = -1

    private var camera: Camera? = null

    private var root: View? = null

    private var isCapturing: Boolean = false

    private var isFlashEnabled: Boolean = false

    private var uriList = arrayListOf<Uri>()

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        override fun onDisplayChanged(displayId: Int) {
            if (this@CameraActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation =
                        root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }
            }
        }
    }

    private val imagePreviewLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, result.data)
                finish()
            }
        }

//    setResult(Activity.RESULT_OK, Intent().apply {
//        putExtra(GalleryActivity.URI_LIST_KEY, ArrayList(state.photoList.map { it.uri }))
//    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        root = binding.root

        if (allPermissionsGranted()) {
            startCamera(binding.viewFinder)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(viewFinder: PreviewView) {
        displayManager.registerDisplayListener(displayListener, null)

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }

    private fun bindCameraUseCase() = with(binding) {
        // 회전 값 설정
        val rotation = viewFinder.display.rotation
        // 카메라 후면 설정
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build()

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

            // imageCapture init
            val builder = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setFlashMode(FLASH_MODE_AUTO)

            imageCapture = builder.build()

            try {
                // 기존에 바인딩 되어 있는 카메라는 해제해주어야 한다.
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this@CameraActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)

                bindCaptureListener()
                bindZoomListener()
                bindLightSwitchListener()
                bindPreviewImageViewClickListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
    }

    private fun bindCaptureListener() = with(binding) {
        captureButton.setOnClickListener {
            if (!isCapturing) {
                isCapturing = true
                captureCamera()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindZoomListener() = with(binding) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(this@CameraActivity, listener)

        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private fun bindLightSwitchListener() = with(binding) {
        // 스위치와 변수 연결
        flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            isFlashEnabled = isChecked
        }
    }

    private fun bindPreviewImageViewClickListener() = with(binding) {
        previewImageVIew.setOnClickListener {
            imagePreviewLaunch.launch(
                ImagePreviewListActivity.newIntent(
                    this@CameraActivity,
                    uriList
                )
            )
        }
    }

    private var contentUri: Uri? = null

    private fun captureCamera() {
        if (!::imageCapture.isInitialized) return
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        if (isFlashEnabled) flashLight(true)
        imageCapture.takePicture(outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    val rotation = binding.viewFinder.display.rotation
                    contentUri = savedUri
                    updateSavedImageContent()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCapturing = false
                }
            })
    }

    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null)
                Handler(Looper.getMainLooper()).post {
                    binding.previewImageVIew.load(url = it.toString(), corner = 4f)
                }
                if (isFlashEnabled) flashLight(false)
                uriList.add(it)
                false
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun flashLight(light: Boolean) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit()
        if (true == hasFlash) {
            camera?.cameraControl?.enableTorch(light)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            startCamera(binding.viewFinder)
        } else {
            Toast.makeText(this, "권한을 부여하지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, CameraActivity::class.java)

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS = arrayOf(permission.CAMERA)

        private val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK

        const val CONFIRM_IMAGE_REQUEST_CODE = 3000

        private const val URI_LIST_KEY = "uriList"
    }
}
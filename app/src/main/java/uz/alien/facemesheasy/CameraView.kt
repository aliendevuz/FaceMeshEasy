package uz.alien.facemesheasy


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private lateinit var context: AppCompatActivity

    private val preview = PreviewView(context, attrs)
    private val faceMeshView = FaceMeshView(context, attrs)

    private var cameraExecutor: ExecutorService

    init {
        addView(preview)
        addView(faceMeshView)
        preview.alpha = 0.1f
        preview.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        faceMeshView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), ActivityFaceMeshCamera.REQUEST_CODE_PERMISSIONS)
    }

    fun setActivity(activity: AppCompatActivity) {
        this.context = activity
        if (allPermissionsGranted()) startCamera() else requestPermissions()
    }

    fun allPermissionsGranted() = ActivityFaceMeshCamera.REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(preview.surfaceProvider) }
            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    context.runOnUiThread {
                        val bitmap = this.preview.bitmap
                        if (bitmap != null) faceMeshView.setBitmap(bitmap)
                    }
                    imageProxy.close()
                }
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(context, cameraSelector, preview, imageAnalysis)
            } catch(exc: Exception) {
                Toast.makeText(context, "Use case binding failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cameraExecutor.shutdown()
    }
}
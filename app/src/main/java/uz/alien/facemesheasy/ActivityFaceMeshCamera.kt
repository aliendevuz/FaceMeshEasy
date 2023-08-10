package uz.alien.facemesheasy


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.alien.facemesheasy.databinding.ActivityFacemeshCameraBinding


class ActivityFaceMeshCamera : AppCompatActivity() {

    private val context = this
    private lateinit var binding: ActivityFacemeshCameraBinding
    private lateinit var vCamera: CameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacemeshCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vCamera = binding.vCamera
        vCamera.setActivity(context)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (vCamera.allPermissionsGranted())
                vCamera.startCamera()
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 99
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
}
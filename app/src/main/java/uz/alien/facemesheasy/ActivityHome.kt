package uz.alien.facemesheasy


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.alien.facemesheasy.databinding.ActivityHomeBinding


class ActivityHome : AppCompatActivity() {

    private val context = this
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bOpenView.setOnClickListener { startActivity(Intent(context, ActivityFaceMeshView::class.java)) }
        binding.bOpenCamera.setOnClickListener { startActivity(Intent(context, ActivityFaceMeshCamera::class.java)) }
    }
}
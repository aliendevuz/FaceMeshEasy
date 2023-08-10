package uz.alien.facemesheasy


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.alien.facemesheasy.databinding.ActivityFacemeshViewBinding


class ActivityFaceMeshView : AppCompatActivity() {

    private lateinit var binding: ActivityFacemeshViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacemeshViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
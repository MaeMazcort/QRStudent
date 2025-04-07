package com.example.qrstudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityMostrarQrBinding

class MostrarQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMostrarQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMostrarQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlaceholderQR.setOnClickListener {
            finish()
        }
    }
}

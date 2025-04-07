package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityGenerarQrBinding

class GenerarQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerarQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerarQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimularGenerarQR.setOnClickListener {
            Toast.makeText(this, "QR generado exitosamente (simulado)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MostrarQRActivity::class.java))
        }
    }
}

package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityEscanearQrBinding

class EscanearQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscanearQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimularEscanearQR.setOnClickListener {
            Toast.makeText(this, "QR Escaneado (simulado)", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ConfirmarAsistenciaActivity::class.java))
            finish()
        }
    }
}

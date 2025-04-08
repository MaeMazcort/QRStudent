package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityAlumnoDashboardBinding

class AlumnoDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlumnoDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlumnoDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEscanearQR.setOnClickListener {
            startActivity(Intent(this, EscanearQRActivity::class.java))
        }

        binding.btnVerCalificaciones.setOnClickListener {
            startActivity(Intent(this, VerCalificacionesActivity::class.java))
        }

        binding.btnVerHorario.setOnClickListener {
            startActivity(Intent(this, VerHorarioAlumnoActivity::class.java))
        }
    }
}

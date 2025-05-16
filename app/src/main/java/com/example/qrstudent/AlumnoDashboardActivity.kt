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

        binding.btnVerPerros.setOnClickListener {
            val intent = Intent(this, DogActivity::class.java)
            startActivity(intent)
        }

        binding.btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("LOGOUT", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

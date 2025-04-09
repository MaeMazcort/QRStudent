package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityProfesorDashboardBinding

class ProfesorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfesorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfesorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerarQR.setOnClickListener {
            startActivity(Intent(this, GenerarQRActivity::class.java))
        }

        binding.btnAsignarCalificaciones.setOnClickListener {
            startActivity(Intent(this, SeleccionarMateriaActivity::class.java))
        }

        binding.btnVerHorario.setOnClickListener {
            startActivity(Intent(this, VerHorarioProfesorActivity::class.java))
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

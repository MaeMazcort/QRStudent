package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCrearMateria.setOnClickListener {
            startActivity(Intent(this, CrearMateriaActivity::class.java))
        }

        binding.btnGestionarUsuarios.setOnClickListener {
            startActivity(Intent(this, GestionarUsuariosActivity::class.java))
        }

        binding.btnAsignarMateria.setOnClickListener {
            startActivity(Intent(this, AsignarMateriaActivity::class.java))
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

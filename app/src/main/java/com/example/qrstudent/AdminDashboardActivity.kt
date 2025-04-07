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
    }
}

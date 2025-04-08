package com.example.qrstudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityDetalleMateriaBinding

class DetalleMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleMateriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        val nombreMateria = intent.getStringExtra("nombreMateria") ?: "Materia"
        binding.tvNombreMateria.text = nombreMateria

        binding.btnCerrarDetalle.setOnClickListener {
            finish()
        }
    }

}

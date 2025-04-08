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
        supportActionBar?.hide()

        // Obtener datos de la materia
        val materiaNombre = intent.getStringExtra("MATERIA_NOMBRE") ?: "Sin nombre"
        val calificacion = intent.getStringExtra("CALIFICACION") ?: "Sin calificación"

        // Configurar vistas
        binding.tvTituloMateria.text = materiaNombre
        binding.tvCalificacionDetalle.text = "Calificación: $calificacion"

        // Botón de regreso
        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }
}
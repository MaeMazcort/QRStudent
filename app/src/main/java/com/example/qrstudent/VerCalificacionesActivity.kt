package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerCalificacionesBinding

class VerCalificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerCalificacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerCalificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        val materias = listOf(
            MateriaAlumno("Matemáticas", "95"),
            MateriaAlumno("Historia", "88")
        )

        val adapter = MateriaAlumnoAdapter(materias) { materia ->
            val intent = Intent(this, DetalleMateriaActivity::class.java)
            intent.putExtra("nombreMateria", materia.nombre)
            startActivity(intent)
        }

        binding.recyclerCalificaciones.layoutManager = LinearLayoutManager(this)
        binding.recyclerCalificaciones.adapter = adapter
    }
}
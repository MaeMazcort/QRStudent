package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityAsignarCalificacionesBinding

class AsignarCalificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsignarCalificacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignarCalificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        val alumnos = listOf(
            Alumno("Alumno A", ""),
            Alumno("Alumno B", ""),
            Alumno("Alumno C", "")
        )

        val adapter = CalificacionesAdapter(alumnos) { alumno ->
            Toast.makeText(this, "Calificación guardada para ${alumno.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerAlumnos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlumnos.adapter = adapter
    }
}

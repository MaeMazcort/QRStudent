package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerHorarioAlumnoBinding

class VerHorarioAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerHorarioAlumnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerHorarioAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        val horario = listOf(
            MateriaHorario("Matemáticas", "Lunes 9am-11am"),
            MateriaHorario("Historia", "Miércoles 12pm-2pm")
        )

        val adapter = HorarioAdapter(horario) { materia ->
            val intent = Intent(this, DetalleMateriaActivity::class.java)
            intent.putExtra("nombreMateria", materia.nombre)
            startActivity(intent)
        }

        binding.recyclerHorarioAlumno.layoutManager = LinearLayoutManager(this)
        binding.recyclerHorarioAlumno.adapter = adapter
    }
}

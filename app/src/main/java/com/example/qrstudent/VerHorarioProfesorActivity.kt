package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerHorarioProfesorBinding

class VerHorarioProfesorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerHorarioProfesorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerHorarioProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val horario = listOf(
            MateriaHorario("Matemáticas", "Lunes 9am-11am"),
            MateriaHorario("Física", "Martes 12pm-2pm")
        )

        val adapter = HorarioAdapter(horario) { materiaHorario ->
            // acción al dar click (puedes dejarlo vacío si quieres)
            Toast.makeText(this, "Materia: ${materiaHorario.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerHorario.layoutManager = LinearLayoutManager(this)
        binding.recyclerHorario.adapter = adapter
    }
}

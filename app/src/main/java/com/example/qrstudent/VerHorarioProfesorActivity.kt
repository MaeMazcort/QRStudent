package com.example.qrstudent

import android.os.Bundle
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

        val adapter = HorarioAdapter(horario)

        binding.recyclerHorario.layoutManager = LinearLayoutManager(this)
        binding.recyclerHorario.adapter = adapter
    }
}

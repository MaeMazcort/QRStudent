package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityAsignarMateriaBinding

class AsignarMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsignarMateriaBinding
    private lateinit var adapter: MateriasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignarMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        // Simulación de lista de materias existentes
        val listaMaterias = listOf(
            Materia("Matemáticas", "MATE101", "Profesor A"),
            Materia("Historia", "HIST201", "Profesor B"),
            Materia("Ciencias", "CIEN301", "Profesor C")
        )

        adapter = MateriasAdapter(listaMaterias) { materia ->
            Toast.makeText(this, "Materia seleccionada: ${materia.nombre}", Toast.LENGTH_SHORT).show()
            // Aquí abrirías otra pantalla para editar profesor/alumnos asignados
        }

        binding.recyclerMaterias.layoutManager = LinearLayoutManager(this)
        binding.recyclerMaterias.adapter = adapter
    }
}

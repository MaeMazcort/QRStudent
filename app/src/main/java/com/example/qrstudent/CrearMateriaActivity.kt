package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityCrearMateriaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CrearMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearMateriaBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var materiasRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance()
        materiasRef = database.getReference("materias")

        binding.btnGuardarMateria.setOnClickListener {
            val nombreMateria = binding.etNombreMateria.text.toString().trim()
            val codigoMateria = binding.etCodigoMateria.text.toString().trim()
            val descripcionMateria = binding.etDescripcionMateria.text.toString().trim()
            val diasMateria = binding.etDiasMateria.text.toString().trim()
            val horaInicio = binding.etHoraInicio.text.toString().trim()
            val horaFin = binding.etHoraFin.text.toString().trim()

            if(nombreMateria.isEmpty() || codigoMateria.isEmpty() ||
                descripcionMateria.isEmpty() || diasMateria.isEmpty() ||
                horaInicio.isEmpty() || horaFin.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                guardarMateriaEnFirebase(
                    nombreMateria,
                    codigoMateria,
                    descripcionMateria,
                    diasMateria,
                    horaInicio,
                    horaFin
                )
            }
        }
    }

    private fun guardarMateriaEnFirebase(
        nombre: String,
        codigo: String,
        descripcion: String,
        dias: String,
        horaInicio: String,
        horaFin: String
    ) {
        // Generar un ID único para la materia
        val materiaId = materiasRef.push().key ?: return

        // Crear objeto Materia con los nuevos campos
        val materia = hashMapOf(
            "id" to materiaId,
            "nombre" to nombre,
            "codigo" to codigo,
            "descripcion" to descripcion,
            "dias" to dias,
            "horaInicio" to horaInicio,
            "horaFin" to horaFin,
            "fechaCreacion" to Calendar.getInstance().timeInMillis
        )

        // Guardar en Firebase
        materiasRef.child(materiaId).setValue(materia)
            .addOnSuccessListener {
                Toast.makeText(this, "Materia creada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
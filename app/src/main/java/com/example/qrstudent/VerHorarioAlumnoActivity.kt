package com.example.qrstudent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerHorarioAlumnoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerHorarioAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerHorarioAlumnoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val materiasList = mutableListOf<MateriaHorario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerHorarioAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val adapter = HorarioAdapter2(
            materiasList,
            onClick = { materia ->
                Toast.makeText(
                    this,
                    "${materia.nombre}\n${materia.obtenerHorarioCompleto()}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onVerAlumnosClick = null // Los alumnos no pueden ver otros alumnos
        )

        binding.recyclerHorarioAlumno.layoutManager = LinearLayoutManager(this)
        binding.recyclerHorarioAlumno.adapter = adapter

        binding.progressBar.visibility = View.VISIBLE

        obtenerHorarioDelAlumno(adapter)
    }

    private fun obtenerHorarioDelAlumno(adapter: HorarioAdapter2) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val alumnoId = currentUser.uid
        Log.d("VerHorarioAlumno", "Obteniendo materias del alumno con ID: $alumnoId")

        // Consultar todas las materias
        val materiasRef = database.getReference("materias")

        materiasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                materiasList.clear()

                if (!snapshot.exists()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@VerHorarioAlumnoActivity, "No tienes materias asignadas", Toast.LENGTH_SHORT).show()
                    return
                }

                val totalMaterias = snapshot.childrenCount
                var materiasCargadas = 0

                for (materiaSnapshot in snapshot.children) {
                    val materiaId = materiaSnapshot.key ?: continue

                    // Obtener la lista de alumnos en la materia
                    val alumnosRef = materiaSnapshot.child("alumnos")
                    val alumnos = alumnosRef.children.map { it.value as String }

                    // Verificar si el alumno está inscrito en esta materia
                    if (alumnos.contains(alumnoId)) {
                        // Obtener detalles de la materia
                        val nombre = materiaSnapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                        val codigo = materiaSnapshot.child("codigo").getValue(String::class.java) ?: ""
                        val descripcion = materiaSnapshot.child("descripcion").getValue(String::class.java) ?: ""
                        val dias = materiaSnapshot.child("dias").getValue(String::class.java) ?: ""
                        val horaInicio = materiaSnapshot.child("horaInicio").getValue(String::class.java) ?: ""
                        val horaFin = materiaSnapshot.child("horaFin").getValue(String::class.java) ?: ""
                        val fechaCreacion = materiaSnapshot.child("fechaCreacion").getValue(Long::class.java) ?: 0L

                        val materia = MateriaHorario(
                            id = materiaId,
                            nombre = nombre,
                            codigo = codigo,
                            descripcion = descripcion,
                            dias = dias,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            fechaCreacion = fechaCreacion
                        )

                        materiasList.add(materia)
                    }

                    materiasCargadas++
                    if (materiasCargadas == totalMaterias.toInt()) {
                        binding.progressBar.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@VerHorarioAlumnoActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

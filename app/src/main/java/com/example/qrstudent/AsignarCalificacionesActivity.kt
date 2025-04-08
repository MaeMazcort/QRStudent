package com.example.qrstudent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityAsignarCalificacionesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AsignarCalificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsignarCalificacionesBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: CalificacionesAdapter
    private val alumnos = mutableListOf<Alumno>()
    private lateinit var materiaId: String
    private lateinit var profesorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignarCalificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()

        // Obtener parámetros de la actividad anterior
        materiaId = intent.getStringExtra("MATERIA_ID") ?: run {
            Toast.makeText(this, "No se especificó la materia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        profesorId = intent.getStringExtra("PROFESOR_ID") ?: run {
            Toast.makeText(this, "No se identificó al profesor", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar título si existe
        val materiaNombre = intent.getStringExtra("MATERIA_NOMBRE") ?: "Asignar Calificaciones"
        binding.tvTituloMateria.text = "Calificaciones: $materiaNombre"

        // Configurar RecyclerView
        adapter = CalificacionesAdapter(alumnos) { alumno ->
            guardarCalificacion(alumno)
        }

        binding.recyclerAlumnos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlumnos.adapter = adapter

        // Mostrar loader
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoAlumnos.visibility = View.GONE

        // Cargar alumnos de la materia
        cargarAlumnosPorMateria()
    }

    private fun cargarAlumnosPorMateria() {
        val materiaRef = database.getReference("materias").child(materiaId)

        materiaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profesorIdMateria = snapshot.child("profesorId").getValue(String::class.java)
                if (profesorIdMateria != profesorId) {
                    Toast.makeText(
                        this@AsignarCalificacionesActivity,
                        "No tienes permiso para ver esta materia",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    finish()
                    return
                }

                val alumnosNode = snapshot.child("alumnos")

                Log.d("FirebaseDebug", "alumnosNode exists: ${alumnosNode.exists()}")
                Log.d("FirebaseDebug", "alumnosNode value: ${alumnosNode.value}")
                Log.d("FirebaseDebug", "alumnosNode childrenCount: ${alumnosNode.childrenCount}")

                if (!alumnosNode.exists() || alumnosNode.childrenCount <= 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoAlumnos.visibility = View.VISIBLE
                    return
                }

                // Obtener los IDs de los alumnos desde el campo "Valor"
                val alumnosIds = mutableListOf<String>()
                for (alumnoSnapshot in alumnosNode.children) {
                    val alumnoId = alumnoSnapshot.getValue(String::class.java)
                    if (!alumnoId.isNullOrEmpty()) {
                        alumnosIds.add(alumnoId)
                        Log.d("FirebaseDebug", "Alumno ID encontrado: $alumnoId")
                    }
                }


                if (alumnosIds.isEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoAlumnos.visibility = View.VISIBLE
                    return
                }

                var alumnosCargados = 0

                for (alumnoId in alumnosIds) {
                    val usuarioRef = database.getReference("users").child(alumnoId)
                    usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(alumnoSnapshot: DataSnapshot) {
                            try {
                                val rol = alumnoSnapshot.child("role").getValue(String::class.java)
                                if (rol != "Alumno") {
                                    Log.d("FirebaseDebug", "Usuario $alumnoId no es Alumno")
                                    alumnosCargados++
                                    verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                                    return
                                }

                                val nombre = alumnoSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                                val email = alumnoSnapshot.child("email").getValue(String::class.java) ?: ""

                                val calificacionRef = database.getReference("calificaciones")
                                    .child(materiaId).child(alumnoId)

                                calificacionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(calificacionSnapshot: DataSnapshot) {
                                        val calificacion = calificacionSnapshot.getValue(String::class.java) ?: ""

                                        val alumno = Alumno(
                                            id = alumnoId,
                                            nombre = nombre,
                                            email = email,
                                            calificacion = calificacion
                                        )
                                        alumnos.add(alumno)
                                        alumnosCargados++
                                        verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("FirebaseDebug", "Error al cargar calificación: ${error.message}")
                                        alumnosCargados++
                                        verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                                    }
                                })
                            } catch (e: Exception) {
                                Log.e("FirebaseDebug", "Error al procesar alumno: ${e.message}")
                                alumnosCargados++
                                verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseDebug", "Error al obtener alumno: ${error.message}")
                            alumnosCargados++
                            verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Error al cargar materia: ${error.message}")
                binding.progressBar.visibility = View.GONE
                binding.tvNoAlumnos.visibility = View.VISIBLE
                Toast.makeText(
                    this@AsignarCalificacionesActivity,
                    "Error al cargar alumnos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun verificarCargaCompleta(cargados: Int, total: Int) {
        if (cargados >= total) {
            binding.progressBar.visibility = View.GONE
            if (alumnos.isEmpty()) {
                binding.tvNoAlumnos.visibility = View.VISIBLE
            } else {
                adapter.notifyDataSetChanged()
            }
        }
    }



    private fun guardarCalificacion(alumno: Alumno) {
        binding.progressBar.visibility = View.VISIBLE

        // Guardar calificación en Firebase
        database.getReference("calificaciones")
            .child(materiaId)
            .child(alumno.id)
            .setValue(alumno.calificacion)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Calificación guardada para ${alumno.nombre}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Error al guardar calificación: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("FirebaseDebug", "Error al guardar calificación: ${e.message}")
            }
    }
}
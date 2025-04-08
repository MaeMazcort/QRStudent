package com.example.qrstudent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerAlumnosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerAlumnosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerAlumnosBinding
    private lateinit var database: FirebaseDatabase
    private val alumnosList = mutableListOf<Usuario>()
    private var materiaId: String = ""
    private var materiaNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerAlumnosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Obtener parámetros
        materiaId = intent.getStringExtra("MATERIA_ID") ?: ""
        materiaNombre = intent.getStringExtra("MATERIA_NOMBRE") ?: "Materia"

        if (materiaId.isEmpty()) {
            Toast.makeText(this, "ID de materia no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Título con el nombre de la materia
        binding.tvTituloMateria.text = "Alumnos de $materiaNombre"

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()

        // Configurar RecyclerView
        val adapter = AlumnosAdapter(alumnosList)
        binding.recyclerAlumnos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAlumnos.adapter = adapter

        // Botón para regresar
        binding.btnRegresar.setOnClickListener {
            finish()
        }

        // Mostrar indicador de carga
        binding.progressBar.visibility = View.VISIBLE

        // Cargar alumnos de la materia
        cargarAlumnosDeMateria(adapter)
    }

    private fun cargarAlumnosDeMateria(adapter: AlumnosAdapter) {
        // Primero obtenemos los IDs de los alumnos asignados a esta materia
        database.getReference("materias").child(materiaId).child("alumnos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNoAlumnos.visibility = View.VISIBLE
                        return
                    }

                    val alumnosIds = mutableListOf<String>()
                    for (alumnoSnapshot in snapshot.children) {
                        val alumnoId = alumnoSnapshot.getValue(String::class.java)
                        if (alumnoId != null) {
                            alumnosIds.add(alumnoId)
                        }
                    }

                    if (alumnosIds.isEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNoAlumnos.visibility = View.VISIBLE
                        return
                    }

                    // Luego obtenemos los detalles de cada alumno
                    cargarDetallesAlumnos(alumnosIds, adapter)
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@VerAlumnosActivity,
                        "Error al cargar alumnos: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun cargarDetallesAlumnos(alumnosIds: List<String>, adapter: AlumnosAdapter) {
        alumnosList.clear()
        var alumnosCargados = 0

        for (alumnoId in alumnosIds) {
            database.getReference("users").child(alumnoId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val name = snapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                            val email = snapshot.child("email").getValue(String::class.java) ?: "Sin email"
                            val role = snapshot.child("role").getValue(String::class.java) ?: "Desconocido"

                            val alumno = Usuario(alumnoId, name, email, role)
                            alumnosList.add(alumno)
                            Log.d("VerAlumnos", "Alumno cargado: $name")

                        } catch (e: Exception) {
                            Log.e("VerAlumnos", "Error al procesar alumno", e)
                        }

                        alumnosCargados++
                        if (alumnosCargados >= alumnosIds.size) {
                            // Todos los alumnos han sido cargados
                            binding.progressBar.visibility = View.GONE
                            if (alumnosList.isEmpty()) {
                                binding.tvNoAlumnos.visibility = View.VISIBLE
                            } else {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        alumnosCargados++
                        if (alumnosCargados >= alumnosIds.size) {
                            binding.progressBar.visibility = View.GONE
                            if (alumnosList.isEmpty()) {
                                binding.tvNoAlumnos.visibility = View.VISIBLE
                            }
                        }
                        Log.e("VerAlumnos", "Error al cargar alumno", error.toException())
                    }
                })
        }
    }
}
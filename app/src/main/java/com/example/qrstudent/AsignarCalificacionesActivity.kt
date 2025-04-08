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
import android.content.Context
import androidx.appcompat.app.AlertDialog


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

                // Accede a los alumnos de la materia
                val alumnosNode = snapshot.child("alumnos")

                // Si no hay alumnos o el nodo está vacío, muestra mensaje
                if (!alumnosNode.exists() || alumnosNode.childrenCount <= 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoAlumnos.visibility = View.VISIBLE
                    return
                }

                val alumnosIds = mutableListOf<String>()
                for (alumnoSnapshot in alumnosNode.children) {
                    val alumnoId = alumnoSnapshot.getValue(String::class.java)
                    if (!alumnoId.isNullOrEmpty()) {
                        alumnosIds.add(alumnoId)
                    }
                }

                if (alumnosIds.isEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoAlumnos.visibility = View.VISIBLE
                    return
                }

                var alumnosCargados = 0

                for (alumnoId in alumnosIds) {
                    // Accede al nodo "users" para obtener los detalles del alumno
                    val usuarioRef = database.getReference("users").child(alumnoId)
                    usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(alumnoSnapshot: DataSnapshot) {
                            val rol = alumnoSnapshot.child("role").getValue(String::class.java)
                            if (rol != "Alumno") {
                                alumnosCargados++
                                verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                                return
                            }

                            val nombre = alumnoSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                            val email = alumnoSnapshot.child("email").getValue(String::class.java) ?: ""

                            // Carga las calificaciones del alumno
                            val calificacionRef = database.getReference("calificaciones")
                                .child(materiaId).child(alumnoId)

                            calificacionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(calificacionSnapshot: DataSnapshot) {
                                    val calificacion = calificacionSnapshot.getValue(String::class.java) ?: ""

                                    // Crear el objeto Alumno con la información cargada
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
                                    alumnosCargados++
                                    verificarCargaCompleta(alumnosCargados, alumnosIds.size)
                                }
                            })
                        }

                        override fun onCancelled(error: DatabaseError) {
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

    fun verHorarioAlumno(alumnoId: String) {
        val context = this@AsignarCalificacionesActivity
        val usuarioRef = database.getReference("users").child(alumnoId)

        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val materiasNode = snapshot.child("materias")
                if (!materiasNode.exists()) {
                    Toast.makeText(
                        context,
                        "El alumno no tiene materias asignadas",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val materiaInfoList = mutableListOf<String>()
                var materiasCargadas = 0
                val totalMaterias = materiasNode.childrenCount.toInt()

                for (materiaSnapshot in materiasNode.children) {
                    val materiaId = materiaSnapshot.key ?: continue

                    val materiaRef = database.getReference("materias").child(materiaId)
                    materiaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(materiaData: DataSnapshot) {
                            val nombre = materiaData.child("nombre").getValue(String::class.java)
                                ?: "Sin nombre"
                            val horario = materiaData.child("horario").getValue(String::class.java)
                                ?: "Sin horario"
                            val descripcion =
                                materiaData.child("descripcion").getValue(String::class.java)
                                    ?: "Sin descripción"

                            val info = "📘 $nombre\n🕒 Horario: $horario\n📝 $descripcion"
                            materiaInfoList.add(info)

                            materiasCargadas++
                            if (materiasCargadas == totalMaterias) {
                                mostrarMateriasDialog(materiaInfoList, context)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            materiasCargadas++
                            Log.e("verHorarioAlumno", "Error al cargar materia: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al obtener datos del alumno", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun mostrarMateriasDialog(materias: List<String>, context: Context) {
        val mensaje = if (materias.isNotEmpty()) {
            materias.joinToString("\n\n")
        } else {
            "El alumno no tiene materias registradas."
        }

        AlertDialog.Builder(context)
            .setTitle("Materias del Alumno")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}

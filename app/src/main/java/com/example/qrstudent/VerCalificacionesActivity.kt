package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerCalificacionesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerCalificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerCalificacionesBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val materiasList = mutableListOf<MateriaAlumno>()
    private lateinit var adapter: MateriaAlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerCalificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Configurar RecyclerView
        adapter = MateriaAlumnoAdapter(materiasList) { materia ->
            val intent = Intent(this, DetalleMateriaActivity::class.java)
            intent.putExtra("MATERIA_NOMBRE", materia.nombre)
            intent.putExtra("CALIFICACION", materia.calificacion)
            startActivity(intent)
        }

        binding.recyclerMaterias.layoutManager = LinearLayoutManager(this)
        binding.recyclerMaterias.adapter = adapter

        // Mostrar loader
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoMaterias.visibility = View.GONE

        // Cargar materias del alumno actual
        cargarMateriasDelAlumno()
    }

    private fun cargarMateriasDelAlumno() {
        val alumnoId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Referencia para buscar todas las materias
        val materiasRef = database.getReference("materias")
        materiasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val materiasEncontradas = mutableListOf<String>()

                // Buscar todas las materias donde el alumno está inscrito
                for (materiaSnapshot in snapshot.children) {
                    val materiaId = materiaSnapshot.key ?: continue
                    val alumnosNode = materiaSnapshot.child("alumnos")

                    for (alumnoSnapshot in alumnosNode.children) {
                        val id = alumnoSnapshot.getValue(String::class.java)
                        if (id == alumnoId) {
                            materiasEncontradas.add(materiaId)
                            break
                        }
                    }
                }

                if (materiasEncontradas.isEmpty()) {
                    mostrarNoHayMaterias()
                    return
                }

                var materiasCargadas = 0

                // Cargar detalles de cada materia encontrada
                for (materiaId in materiasEncontradas) {
                    cargarDetallesMateria(materiaId, alumnoId) {
                        materiasCargadas++
                        if (materiasCargadas >= materiasEncontradas.size) {
                            binding.progressBar.visibility = View.GONE
                            if (materiasList.isEmpty()) {
                                binding.tvNoMaterias.visibility = View.VISIBLE
                            } else {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Error al cargar materias: ${error.message}")
                mostrarNoHayMaterias()
            }
        })
    }

    private fun cargarDetallesMateria(materiaId: String, alumnoId: String, onComplete: () -> Unit) {
        val materiaRef = database.getReference("materias").child(materiaId)

        materiaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = snapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"

                // Cargar calificación
                val calificacionRef = database.getReference("calificaciones")
                    .child(materiaId).child(alumnoId)

                calificacionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(calificacionSnapshot: DataSnapshot) {
                        val calificacion = calificacionSnapshot.getValue(String::class.java) ?: "Sin calificación"

                        val materiaAlumno = MateriaAlumno(
                            nombre = nombre,
                            calificacion = calificacion
                        )
                        materiasList.add(materiaAlumno)
                        onComplete()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseDebug", "Error al cargar calificación: ${error.message}")
                        onComplete()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Error al cargar materia: ${error.message}")
                onComplete()
            }
        })
    }

    private fun mostrarNoHayMaterias() {
        binding.progressBar.visibility = View.GONE
        binding.tvNoMaterias.visibility = View.VISIBLE
    }
}
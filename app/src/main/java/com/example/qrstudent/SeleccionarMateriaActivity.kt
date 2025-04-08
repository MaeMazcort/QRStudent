package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivitySeleccionarMateriaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SeleccionarMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccionarMateriaBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: MateriasAdapter
    private val materias = mutableListOf<Materia>()
    private lateinit var profesorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()



        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()

        // Obtener ID del profesor actual
        profesorId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "No se identificó al profesor", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar RecyclerView
        adapter = MateriasAdapter(materias) { materia ->
            if (materia.alumnos.isNotEmpty()) {
                val intent = Intent(this, AsignarCalificacionesActivity::class.java).apply {
                    putExtra("MATERIA_ID", materia.id)
                    putExtra("PROFESOR_ID", profesorId)
                    putExtra("MATERIA_NOMBRE", materia.nombre)  // Añade el nombre de la materia
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Esta materia no tiene alumnos asignados", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerMaterias.layoutManager = LinearLayoutManager(this)
        binding.recyclerMaterias.adapter = adapter

        // Mostrar loader
        binding.progressBar.visibility = View.VISIBLE

        // Cargar materias del profesor
        cargarMateriasPorProfesor()
    }

    private fun cargarMateriasPorProfesor() {
        database.getReference("materias")
            .orderByChild("profesorId")
            .equalTo(profesorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    materias.clear()

                    for (materiaSnapshot in snapshot.children) {
                        try {
                            val materia = Materia(
                                id = materiaSnapshot.key ?: continue,
                                nombre = materiaSnapshot.child("nombre").getValue(String::class.java) ?: "Sin Nombre",
                                codigo = materiaSnapshot.child("codigo").getValue(String::class.java) ?: "",
                                descripcion = materiaSnapshot.child("descripcion").getValue(String::class.java) ?: "",
                                fechaCreacion = materiaSnapshot.child("fechaCreacion").getValue(Long::class.java) ?: 0L,
                                profesorId = materiaSnapshot.child("profesorId").getValue(String::class.java) ?: "",
                                profesorNombre = materiaSnapshot.child("profesorNombre").getValue(String::class.java) ?: "",
                                alumnos = materiaSnapshot.child("alumnos").children.mapNotNull { it.key }
                            )
                            materias.add(materia)
                        } catch (e: Exception) {
                            Log.e("SeleccionarMateria", "Error al parsear materia: ${e.message}")
                        }
                    }

                    binding.progressBar.visibility = View.GONE

                    if (materias.isEmpty()) {
                        binding.tvNoMaterias.visibility = View.VISIBLE
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@SeleccionarMateriaActivity,
                        "Error al cargar materias: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SeleccionarMateria", error.message)
                }
            })
    }
}
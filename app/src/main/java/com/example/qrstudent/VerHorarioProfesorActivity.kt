package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityVerHorarioProfesorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerHorarioProfesorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerHorarioProfesorBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val materiasList = mutableListOf<MateriaHorario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerHorarioProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Configurar RecyclerView
        val adapter = HorarioAdapter(
            materiasList,
            onClick = { materia ->
                Toast.makeText(
                    this,
                    "${materia.nombre}\n${materia.obtenerHorarioCompleto()}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onVerAlumnosClick = { materia ->
                // Navegar a la actividad para ver los alumnos de esta materia
                val intent = Intent(this, VerAlumnosActivity::class.java).apply {
                    putExtra("MATERIA_ID", materia.id)
                    putExtra("MATERIA_NOMBRE", materia.nombre)
                }
                startActivity(intent)
            }
        )

        binding.recyclerHorario.layoutManager = LinearLayoutManager(this)
        binding.recyclerHorario.adapter = adapter

        // Mostrar el indicador de carga
        binding.progressBar.visibility = View.VISIBLE

        // Obtener horario del profesor desde Firebase
        obtenerHorarioDelProfesor(adapter)
    }

    private fun obtenerHorarioDelProfesor(adapter: HorarioAdapter) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Error: Usuario no autenticado",
                Toast.LENGTH_SHORT
            ).show()
            finish() // Regresar a la actividad anterior si no hay usuario autenticado
            return
        }

        val profesorId = currentUser.uid
        Log.d("VerHorarioProfesor", "Obteniendo horario para profesor con ID: $profesorId")

        database.getReference("materias")
            .orderByChild("profesorId")
            .equalTo(profesorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    materiasList.clear()

                    // Ocultar el indicador de carga
                    binding.progressBar.visibility = View.GONE

                    if (!snapshot.exists() || snapshot.childrenCount <= 0) {
                        Log.d("VerHorarioProfesor", "No se encontraron materias para este profesor")
                        Toast.makeText(
                            this@VerHorarioProfesorActivity,
                            "No se encontraron materias en tu horario",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    for (materiaSnapshot in snapshot.children) {
                        try {
                            val materiaId = materiaSnapshot.key ?: continue
                            Log.d("VerHorarioProfesor", "Cargando materia con ID: $materiaId")

                            // Extraer datos directamente del snapshot
                            val nombre = materiaSnapshot.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                            val codigo = materiaSnapshot.child("codigo").getValue(String::class.java) ?: ""
                            val descripcion = materiaSnapshot.child("descripcion").getValue(String::class.java) ?: ""
                            val dias = materiaSnapshot.child("dias").getValue(String::class.java) ?: ""
                            val horaInicio = materiaSnapshot.child("horaInicio").getValue(String::class.java) ?: ""
                            val horaFin = materiaSnapshot.child("horaFin").getValue(String::class.java) ?: ""
                            val fechaCreacion = materiaSnapshot.child("fechaCreacion").getValue(Long::class.java) ?: 0L
                            val profesorId = materiaSnapshot.child("profesorId").getValue(String::class.java) ?: ""
                            val profesorNombre = materiaSnapshot.child("profesorNombre").getValue(String::class.java) ?: ""

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
                            Log.d("VerHorarioProfesor", "Materia agregada: ${materia.nombre}")
                        } catch (e: Exception) {
                            Log.e("VerHorarioProfesor", "Error al procesar materia: ${e.message}", e)
                        }
                    }

                    // Notificar al adaptador solo una vez después de cargar todas las materias
                    adapter.notifyDataSetChanged()
                    Log.d("VerHorarioProfesor", "Total materias cargadas: ${materiasList.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Ocultar el indicador de carga
                    binding.progressBar.visibility = View.GONE

                    val errorMessage = "Error al cargar horario: ${error.message} (Código: ${error.code})"
                    Log.e("VerHorarioProfesor", errorMessage)
                    Toast.makeText(
                        this@VerHorarioProfesorActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
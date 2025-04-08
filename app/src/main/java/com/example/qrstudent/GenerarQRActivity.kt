package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityGenerarQrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

class GenerarQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerarQrBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val materiasList = mutableListOf<MateriaHorario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerarQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerMaterias.layoutManager = LinearLayoutManager(this)

        val adapter = HorarioAdapter2(
            materiasList,
            onClick = { materia ->
                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                val qrData = """
                    Materia: ${materia.nombre}
                    Código: ${materia.codigo}
                    Día(s): ${materia.dias}
                    Hora: ${materia.horaInicio} - ${materia.horaFin}
                    Fecha: $fecha
                """.trimIndent()

                val intent = Intent(this, MostrarQRActivity::class.java).apply {
                    putExtra("FECHA_ASISTENCIA", fecha)
                    putExtra("QR_DATA", qrData)
                }
                startActivity(intent)
            },
            onVerAlumnosClick = null // No lo necesitas aquí
        )

        binding.recyclerMaterias.adapter = adapter
        obtenerMateriasDelProfesor(adapter)
    }

    private fun obtenerMateriasDelProfesor(adapter: HorarioAdapter2) {
        val currentUser = auth.currentUser ?: return

        binding.progressBar.visibility = View.VISIBLE

        database.getReference("materias")
            .orderByChild("profesorId")
            .equalTo(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    materiasList.clear()
                    binding.progressBar.visibility = View.GONE

                    for (materiaSnapshot in snapshot.children) {
                        val materiaId = materiaSnapshot.key ?: continue
                        val nombre = materiaSnapshot.child("nombre").getValue(String::class.java) ?: ""
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

                    adapter.notifyDataSetChanged()

                    if (materiasList.isEmpty()) {
                        Toast.makeText(this@GenerarQRActivity, "No se encontraron materias", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@GenerarQRActivity, "Error al cargar materias", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

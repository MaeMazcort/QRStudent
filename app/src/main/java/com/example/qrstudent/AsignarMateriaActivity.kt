package com.example.qrstudent

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityAsignarMateriaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AsignarMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsignarMateriaBinding
    private lateinit var materiasAdapter: MateriasAdapter
    private lateinit var profesoresAdapter: ArrayAdapter<String>
    private lateinit var alumnosAdapter: ArrayAdapter<String>

    private val database = FirebaseDatabase.getInstance().reference
    private val profesoresList = mutableListOf<Usuario>()
    private val alumnosList = mutableListOf<Usuario>()
    private val materiasList = mutableListOf<Materia>()
    private var materiaSeleccionada: Materia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignarMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAdapters()
        setupListeners()
        cargarDatos()
    }

    private fun setupAdapters() {
        // Adapter para materias
        materiasAdapter = MateriasAdapter(materiasList) { materia ->
            materiaSeleccionada = materia
            binding.btnAsignar.isEnabled = true
            Toast.makeText(this, "Materia seleccionada: ${materia.nombre}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerMaterias.layoutManager = LinearLayoutManager(this)
        binding.recyclerMaterias.adapter = materiasAdapter

        // Adapter para profesores (Spinner)
        profesoresAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        profesoresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProfesores.adapter = profesoresAdapter

        // Adapter para alumnos (ListView con selección múltiple)
        alumnosAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, mutableListOf<String>())
        binding.listViewAlumnos.adapter = alumnosAdapter
        binding.listViewAlumnos.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    private fun setupListeners() {
        binding.btnAsignar.setOnClickListener {
            materiaSeleccionada?.let { materia ->
                val profesorPos = binding.spinnerProfesores.selectedItemPosition
                if (profesorPos != Spinner.INVALID_POSITION && profesorPos < profesoresList.size) {
                    val profesorSeleccionado = profesoresList[profesorPos]
                    asignarProfesorAMateria(materia, profesorSeleccionado)
                } else {
                    Toast.makeText(this, "Seleccione un profesor", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Seleccione una materia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatos() {
        // Cargar materias
        database.child("materias").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("AsignarMateria", "Se recibieron ${snapshot.childrenCount} materias")
                materiasList.clear()
                for (materiaSnapshot in snapshot.children) {
                    val materia = materiaSnapshot.getValue(Materia::class.java)
                    materia?.let {
                        it.id = materiaSnapshot.key ?: ""
                        materiasList.add(it)
                    }
                }
                materiasAdapter.actualizarLista(materiasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AsignarMateria", "Error al cargar materias", error.toException())
                Toast.makeText(this@AsignarMateriaActivity,
                    "Error al cargar materias: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Cargar profesores con role = "Profesor" exactamente como se ve en tu base de datos
        database.child("users").orderByChild("role").equalTo("Profesor")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("AsignarMateria", "Se recibieron ${snapshot.childrenCount} profesores")
                    profesoresList.clear()
                    val nombresProfesores = mutableListOf<String>()

                    for (userSnapshot in snapshot.children) {
                        try {
                            val id = userSnapshot.key ?: ""
                            val name = userSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                            val email = userSnapshot.child("email").getValue(String::class.java) ?: "Sin email"
                            val role = userSnapshot.child("role").getValue(String::class.java) ?: "Desconocido"

                            val profesor = Usuario(id, name, email, role)
                            profesoresList.add(profesor)
                            nombresProfesores.add("$name ($email)")

                            Log.d("AsignarMateria", "Profesor cargado: $name")
                        } catch (e: Exception) {
                            Log.e("AsignarMateria", "Error al procesar profesor", e)
                        }
                    }

                    profesoresAdapter.clear()
                    profesoresAdapter.addAll(nombresProfesores)
                    profesoresAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AsignarMateria", "Error al cargar profesores", error.toException())
                    Toast.makeText(this@AsignarMateriaActivity,
                        "Error al cargar profesores: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        // Cargar alumnos con role = "Alumno" exactamente como se ve en tu base de datos
        database.child("users").orderByChild("role").equalTo("Alumno")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("AsignarMateria", "Se recibieron ${snapshot.childrenCount} alumnos")
                    alumnosList.clear()
                    val nombresAlumnos = mutableListOf<String>()

                    for (userSnapshot in snapshot.children) {
                        try {
                            val id = userSnapshot.key ?: ""
                            val name = userSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                            val email = userSnapshot.child("email").getValue(String::class.java) ?: "Sin email"
                            val role = userSnapshot.child("role").getValue(String::class.java) ?: "Desconocido"

                            val alumno = Usuario(id, name, email, role)
                            alumnosList.add(alumno)
                            nombresAlumnos.add("$name ($email)")

                            Log.d("AsignarMateria", "Alumno cargado: $name")
                        } catch (e: Exception) {
                            Log.e("AsignarMateria", "Error al procesar alumno", e)
                        }
                    }

                    alumnosAdapter.clear()
                    alumnosAdapter.addAll(nombresAlumnos)
                    alumnosAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AsignarMateria", "Error al cargar alumnos", error.toException())
                    Toast.makeText(this@AsignarMateriaActivity,
                        "Error al cargar alumnos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun asignarProfesorAMateria(materia: Materia, profesor: Usuario) {
        // Verificar autenticación
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(this, "Debe iniciar sesión para asignar profesores", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar la materia con el ID del profesor
        val updates = hashMapOf<String, Any>(
            "profesorId" to profesor.id,
            "profesorNombre" to profesor.name
        )

        // Mostrar indicador de carga
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Asignando profesor...")
        progressDialog.show()

        database.child("materias").child(materia.id).updateChildren(updates)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,
                    "Profesor asignado correctamente a ${materia.nombre}",
                    Toast.LENGTH_SHORT).show()

                // Asignar alumnos seleccionados
                asignarAlumnosAMateria(materia)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.e("AsignarMateria", "Error al asignar profesor", e)
                Toast.makeText(this,
                    "Error al asignar profesor: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun asignarAlumnosAMateria(materia: Materia) {
        val selectedPositions = binding.listViewAlumnos.checkedItemPositions
        val alumnosAsignados = mutableListOf<String>()

        for (i in 0 until selectedPositions.size()) {
            val position = selectedPositions.keyAt(i)
            if (selectedPositions.valueAt(i) && position < alumnosList.size) {
                alumnosList[position].id.let { idAlumno ->
                    alumnosAsignados.add(idAlumno)
                }
            }
        }

        if (alumnosAsignados.isNotEmpty()) {
            database.child("materias").child(materia.id).child("alumnos")
                .setValue(alumnosAsignados)
                .addOnSuccessListener {
                    Toast.makeText(this,
                        "${alumnosAsignados.size} alumnos asignados a ${materia.nombre}",
                        Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("AsignarMateria", "Error al asignar alumnos", e)
                    Toast.makeText(this,
                        "Error al asignar alumnos: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }
}
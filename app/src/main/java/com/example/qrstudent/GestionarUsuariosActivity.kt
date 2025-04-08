package com.example.qrstudent

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityGestionarUsuariosBinding
import com.google.firebase.database.*

class GestionarUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionarUsuariosBinding
    private lateinit var adapter: UsuariosAdapter
    private val usuariosList = mutableListOf<Usuario>()
    private val database = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionarUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        adapter = UsuariosAdapter(usuariosList) { usuario ->
            mostrarDialogoEditar(usuario)
        }

        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsuarios.adapter = adapter

        cargarUsuariosDesdeFirebase()
    }

    private fun cargarUsuariosDesdeFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuariosList.clear()
                for (userSnapshot in snapshot.children) {
                    val usuario = userSnapshot.getValue(Usuario::class.java)
                    usuario?.let {
                        it.id = userSnapshot.key ?: ""
                        usuariosList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GestionarUsuariosActivity,
                    "Error al cargar usuarios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoEditar(usuario: Usuario) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_usuario, null)
        val edtNombre = dialogView.findViewById<EditText>(R.id.edtNombre)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edtEmail)
        val edtRol = dialogView.findViewById<EditText>(R.id.edtRol)

        edtNombre.setText(usuario.name)
        edtEmail.setText(usuario.email)
        edtRol.setText(usuario.role)

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = edtNombre.text.toString()
                val nuevoEmail = edtEmail.text.toString()
                val nuevoRol = edtRol.text.toString()

                val actualizaciones = mapOf(
                    "name" to nuevoNombre,
                    "email" to nuevoEmail,
                    "role" to nuevoRol
                )

                database.child(usuario.id).updateChildren(actualizaciones)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}

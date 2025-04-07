package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrstudent.databinding.ActivityGestionarUsuariosBinding

class GestionarUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionarUsuariosBinding
    private lateinit var adapter: UsuariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionarUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Simulación lista de usuarios
        val listaUsuarios = listOf(
            Usuario("usuario1@example.com", "Alumno"),
            Usuario("usuario2@example.com", "Profesor"),
            Usuario("usuario3@example.com", "Admin")
        )

        adapter = UsuariosAdapter(listaUsuarios) { usuario ->
            Toast.makeText(this, "Usuario seleccionado: ${usuario.email}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsuarios.adapter = adapter
    }
}

package com.example.qrstudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityCrearMateriaBinding

class CrearMateriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearMateriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearMateriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuardarMateria.setOnClickListener {
            val nombreMateria = binding.etNombreMateria.text.toString()
            val codigoMateria = binding.etCodigoMateria.text.toString()
            val descripcionMateria = binding.etDescripcionMateria.text.toString()

            if(nombreMateria.isEmpty() || codigoMateria.isEmpty() || descripcionMateria.isEmpty()){
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                // Simulación guardado exitoso
                Toast.makeText(this, "Materia creada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

package com.example.qrstudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityConfirmarAsistenciaBinding

class ConfirmarAsistenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmarAsistenciaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmarAsistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCerrar.setOnClickListener {
            finish()
        }
    }
}

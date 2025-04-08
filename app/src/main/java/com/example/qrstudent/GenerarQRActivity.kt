package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityGenerarQrBinding
import java.text.SimpleDateFormat
import java.util.*

class GenerarQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerarQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerarQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimularGenerarQR.setOnClickListener {
            // Obtener fecha actual
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            // Pasar datos a la siguiente actividad
            val intent = Intent(this, MostrarQRActivity::class.java).apply {
                putExtra("FECHA_ASISTENCIA", fecha)
                putExtra("QR_DATA", "Asistencia registrada el $fecha") // Datos del QR
            }
            startActivity(intent)
        }
    }
}
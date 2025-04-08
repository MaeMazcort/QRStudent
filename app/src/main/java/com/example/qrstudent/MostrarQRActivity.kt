package com.example.qrstudent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityMostrarQrBinding
import okhttp3.*
import java.io.IOException

class MostrarQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMostrarQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMostrarQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        val fecha = intent.getStringExtra("FECHA_ASISTENCIA") ?: "N/A"
        val qrData = intent.getStringExtra("QR_DATA") ?: "Error"

        // Mostrar fecha
        binding.tvFecha.text = "QR de asistencia para la fecha: $fecha"

        // Generar QR
        generateQRCode(qrData, 200, 200)

        // Configurar botón de regreso
        binding.btnRegresar.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }

    private fun generateQRCode(data: String, width: Int, height: Int) {
        val url = "https://api.qrserver.com/v1/create-qr-code/?data=${data}&size=${width}x${height}"

        OkHttpClient().newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.ivQRCode.setImageResource(android.R.drawable.ic_dialog_alert)
                    Toast.makeText(this@MostrarQRActivity, "Error al generar QR", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())
                    runOnUiThread { binding.ivQRCode.setImageBitmap(bitmap) }
                } else {
                    runOnUiThread {
                        binding.ivQRCode.setImageResource(android.R.drawable.ic_dialog_alert)
                        Toast.makeText(this@MostrarQRActivity, "Respuesta inválida", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
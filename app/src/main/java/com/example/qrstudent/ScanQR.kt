package com.example.qrstudent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.json.JSONObject

class ScanQR : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private var isScanned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scann_qr)

        previewView = findViewById(R.id.previewView)

        //Permisos para la aplicación
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    //Comienza el escaneo
    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !isScanned) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val rawValue = barcode.rawValue
                                    if (rawValue != null) {
                                        isScanned = true
                                        procesarQRyRegistrarAsistencia(rawValue)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al escanear", Toast.LENGTH_SHORT).show()
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }

    //Procesamiento del QR para crear el json
    private fun procesarQRyRegistrarAsistencia(rawValue: String) {
        try {
            val json = JSONObject(rawValue)
            val materia = json.getString("materia")
            val codigoClase = json.getString("codigoClase")
            val fechaAsistencia = json.getString("fechaAsistencia")

            val alumnoUid = FirebaseAuth.getInstance().currentUser?.uid
            if (alumnoUid != null) {
                val asistenciaRef = FirebaseDatabase.getInstance().getReference("asistencias")
                    .child(codigoClase)
                    .child(fechaAsistencia)
                    .child(alumnoUid)

                val asistenciaData = mapOf(
                    "uidAlumno" to alumnoUid,
                    "materia" to materia,
                    "fecha" to fechaAsistencia,
                    "timestamp" to ServerValue.TIMESTAMP
                )

                asistenciaRef.setValue(asistenciaData)
                    .addOnSuccessListener {
                        showSuccessDialog()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("ScanQR", "Error al registrar asistencia", e)
                    }
            } else {
                Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar QR: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ScanQR", "Error al procesar QR", e)
        }
    }

    //Alerta de escaneo funcional
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("¡Éxito!")
            .setMessage("Has pasado lista exitosamente")
            .setPositiveButton("Aceptar") { dialog, _ ->
                val intent = Intent(this, AlumnoDashboardActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}

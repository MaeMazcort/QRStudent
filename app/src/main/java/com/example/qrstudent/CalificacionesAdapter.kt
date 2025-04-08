package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemAlumnoCalificacionBinding

class CalificacionesAdapter(
    private val alumnos: List<Alumno>,
    private val onCalificacionSaved: (Alumno) -> Unit

) : RecyclerView.Adapter<CalificacionesAdapter.CalificacionViewHolder>() {

    inner class CalificacionViewHolder(val binding: ItemAlumnoCalificacionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalificacionViewHolder {
        val binding = ItemAlumnoCalificacionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return CalificacionViewHolder(binding)
    }

    override fun getItemCount() = alumnos.size

    override fun onBindViewHolder(holder: CalificacionViewHolder, position: Int) {
        val alumno = alumnos[position]

        holder.binding.tvAlumnoNombre.text = alumno.nombre

        holder.binding.btnGuardarCalificacion.setOnClickListener {
            val calif = holder.binding.etCalificacion.text.toString()

            if(calif.isNotEmpty()){
                alumno.calificacion = calif
                onCalificacionSaved(alumno)
            } else {
                Toast.makeText(holder.itemView.context, "Ingrese calificación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

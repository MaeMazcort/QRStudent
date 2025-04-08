package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemAlumnoBinding

class AlumnosAdapter(
    private val alumnos: List<Usuario>
) : RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(val binding: ItemAlumnoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun getItemCount() = alumnos.size

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        with(holder.binding) {
            tvAlumnoNombre.text = alumno.name
            tvAlumnoEmail.text = alumno.email
        }
        // Usar 'binding.root' para acceder a la vista
        holder.binding.root.setOnClickListener {
            val context = it.context
            if (context is AsignarCalificacionesActivity) {
                context.verHorarioAlumno(alumno.id)
            }
        }
    }
}

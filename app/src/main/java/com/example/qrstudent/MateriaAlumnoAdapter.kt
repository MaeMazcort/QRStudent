package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaAlumnoBinding

class MateriaAlumnoAdapter(
    private val materias: List<MateriaAlumno>,
    private val onClick: (MateriaAlumno) -> Unit
) : RecyclerView.Adapter<MateriaAlumnoAdapter.MateriaAlumnoViewHolder>() {

    inner class MateriaAlumnoViewHolder(val binding: ItemMateriaAlumnoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaAlumnoViewHolder {
        val binding = ItemMateriaAlumnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateriaAlumnoViewHolder(binding)
    }

    override fun getItemCount() = materias.size

    override fun onBindViewHolder(holder: MateriaAlumnoViewHolder, position: Int) {
        val materia = materias[position]
        holder.binding.tvMateriaNombre.text = materia.nombre
        holder.binding.tvCalificacion.text = "Calificación: ${materia.calificacion}"
        holder.itemView.setOnClickListener { onClick(materia) }
    }
}

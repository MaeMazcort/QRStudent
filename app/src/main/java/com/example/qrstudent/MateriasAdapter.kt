package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaBinding

class MateriasAdapter(
    private val materias: List<Materia>,
    private val onClick: (Materia) -> Unit
) : RecyclerView.Adapter<MateriasAdapter.MateriaViewHolder>() {

    inner class MateriaViewHolder(val binding: ItemMateriaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val binding = ItemMateriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateriaViewHolder(binding)
    }

    override fun getItemCount() = materias.size

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val materia = materias[position]
        holder.binding.tvNombreMateria.text = materia.nombre
        holder.binding.tvCodigoMateria.text = materia.codigo
        holder.binding.tvProfesorAsignado.text = materia.profesorAsignado
        holder.itemView.setOnClickListener { onClick(materia) }
    }
}

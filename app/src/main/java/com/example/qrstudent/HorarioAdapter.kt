package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaHorarioBinding

class HorarioAdapter(
    private val horario: List<MateriaHorario>,
    private val onClick: (MateriaHorario) -> Unit
) : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {

    inner class HorarioViewHolder(val binding: ItemMateriaHorarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemMateriaHorarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorarioViewHolder(binding)
    }

    override fun getItemCount() = horario.size

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val materia = horario[position]
        holder.binding.tvMateriaNombre.text = materia.nombre
        holder.binding.tvMateriaHorario.text = materia.horario
        holder.itemView.setOnClickListener { onClick(materia) }
    }
}


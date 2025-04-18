package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaHorarioBinding

class HorarioAdapter(
    private val horario: List<MateriaHorario>,
    private val onClick: (MateriaHorario) -> Unit,
    private val onVerAlumnosClick: ((MateriaHorario) -> Unit)? = null
) : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {

    inner class HorarioViewHolder(val binding: ItemMateriaHorarioBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemMateriaHorarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HorarioViewHolder(binding)
    }

    override fun getItemCount() = horario.size

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val materia = horario[position]
        with(holder.binding) {
            tvMateriaNombre.text = materia.nombre
            tvMateriaCodigo.text = materia.codigo
            tvMateriaHorario.text = materia.obtenerHorarioCompleto()
            tvMateriaDescripcion.text = materia.descripcion

            // Listener para todo el elemento
            root.setOnClickListener { onClick(materia) }

            // Listener específico para el botón Ver
            btnVerAlumnos.setOnClickListener { onVerAlumnosClick?.invoke(materia) }
        }
    }
}
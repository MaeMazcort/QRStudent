package com.example.qrstudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaHorarioAlumnoBinding

class HorarioAdapter2(
    private val horario: List<MateriaHorario>,
    private val onClick: (MateriaHorario) -> Unit,
    private val onVerAlumnosClick: ((MateriaHorario) -> Unit)? = null
) : RecyclerView.Adapter<HorarioAdapter2.HorarioViewHolder>() {

    // ViewHolder que usaremos
    inner class HorarioViewHolder(val binding: ItemMateriaHorarioAlumnoBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Este método crea el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemMateriaHorarioAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HorarioViewHolder(binding)
    }

    // Este método se utiliza para enlazar los datos con las vistas
    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val materia = horario[position]
        with(holder.binding) {
            tvMateriaNombre.text = materia.nombre
            tvMateriaCodigo.text = materia.codigo
            tvMateriaHorario.text = materia.obtenerHorarioCompleto()
            tvMateriaDescripcion.text = materia.descripcion

            // Listener para todo el elemento
            root.setOnClickListener { onClick(materia) }

            // Si se proporciona un onVerAlumnosClick, se puede usar (en este caso no lo usas)

        }
    }

    // Retorna el tamaño de la lista de materias
    override fun getItemCount() = horario.size
}

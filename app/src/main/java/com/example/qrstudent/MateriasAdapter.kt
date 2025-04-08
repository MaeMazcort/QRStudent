package com.example.qrstudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrstudent.databinding.ItemMateriaBinding

class MateriasAdapter(
    private var materias: List<Materia>,
    private val onClick: (Materia) -> Unit
) : RecyclerView.Adapter<MateriasAdapter.MateriaViewHolder>() {

    inner class MateriaViewHolder(val binding: ItemMateriaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val binding = ItemMateriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val materia = materias[position]
        with(holder.binding) {
            txtMateriaNombre.text = materia.nombre
            txtCodigo.text = materia.codigo

            // Mostrar profesor si está asignado
            if (materia.profesorNombre.isNotEmpty()) {
                txtProfesor.text = "Profesor: ${materia.profesorNombre}"
                txtProfesor.visibility = View.VISIBLE
            } else {
                txtProfesor.visibility = View.GONE
            }

            // Mostrar cantidad de alumnos si hay
            if (materia.alumnos.isNotEmpty()) {
                txtAlumnos.text = "Alumnos: ${materia.alumnos.size}"
                txtAlumnos.visibility = View.VISIBLE
            } else {
                txtAlumnos.visibility = View.GONE
            }

            root.setOnClickListener { onClick(materia) }
        }
    }

    override fun getItemCount() = materias.size

    fun actualizarLista(nuevaLista: List<Materia>) {
        materias = nuevaLista
        notifyDataSetChanged()
    }
}
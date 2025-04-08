package com.example.qrstudent

data class MateriaHorario(
    val id: String = "",
    val nombre: String = "",
    val codigo: String = "",
    val descripcion: String = "",
    val dias: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val fechaCreacion: Long = 0
) {
    // Función para formatear el horario completo
    fun obtenerHorarioCompleto(): String {
        return "$dias de $horaInicio a $horaFin"
    }
}
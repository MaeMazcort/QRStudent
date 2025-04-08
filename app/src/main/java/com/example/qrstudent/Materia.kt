package com.example.qrstudent

data class Materia(
    var id: String = "",
    val nombre: String = "",
    val codigo: String = "",
    val descripcion: String = "",
    val fechaCreacion: Long = 0L,
    var profesorId: String = "",
    var profesorNombre: String = "",
    var alumnos: List<String> = emptyList()
)
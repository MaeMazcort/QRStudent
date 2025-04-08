package com.example.qrstudent

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = ""
){
    override fun toString(): String {
        return "$nombre ($email)"
    }
}

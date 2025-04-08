package com.example.qrstudent

data class Usuario(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = ""
) {
    override fun toString(): String {
        return "$name ($email)"
    }
}
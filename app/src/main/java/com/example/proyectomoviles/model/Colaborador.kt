package com.example.proyectomoviles.model

// Modelo de datos para el Colaborador
// Ajustado para coincidir con un GUID (String) en lugar de Int
data class Colaborador(
    val id: String,  // Cambiado de Int a String para soportar GUIDs
    val nombre: String,
    val rol: String,
    val departamento: String,
    val skills: List<Skill>,
    val certificaciones: List<String>,
    val disponible: Boolean
)

data class Skill(
    val nombre: String,
    val nivel: Float // Valor de 0.0 a 1.0
)
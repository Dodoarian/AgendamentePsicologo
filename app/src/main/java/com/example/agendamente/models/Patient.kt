package com.example.agendamente.models

data class Patient(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val psychologistId: String = "",
    val firebaseToken: String = ""
)
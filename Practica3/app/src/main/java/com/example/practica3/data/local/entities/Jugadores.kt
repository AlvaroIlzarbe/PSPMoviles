package com.example.practica3.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "jugadores"
)
data class JugadoresEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val region: String

)
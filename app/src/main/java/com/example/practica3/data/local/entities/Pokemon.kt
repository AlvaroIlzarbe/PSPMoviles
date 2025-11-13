package com.example.practica3.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Pokemon"
)
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true)
    val name: String,
    val type: String,
    val id: Int
)
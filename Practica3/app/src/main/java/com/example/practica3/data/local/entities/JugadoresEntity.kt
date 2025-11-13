package com.example.practica3.data.local.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.practica3.domain.model.Jugadores
import com.example.practica3.domain.model.Pokemon


@Entity(
    tableName = "jugadores",
    foreignKeys = [
        ForeignKey(
            entity = PokemonEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class JugadoresEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val region: String
)

fun Jugadores.toJugadoresEntity() = JugadoresEntity(
    id = this.id,
    nombre = this.nombre,
    region = this.region,

)

fun JugadoresEntity.toJugadores() = Jugadores(
    id = this.id,
    nombre = this.nombre,
    region = this.region
)










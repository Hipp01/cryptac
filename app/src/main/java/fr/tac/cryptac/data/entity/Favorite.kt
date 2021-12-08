package fr.tac.cryptac.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = false)
    val cryptoId: Int
)

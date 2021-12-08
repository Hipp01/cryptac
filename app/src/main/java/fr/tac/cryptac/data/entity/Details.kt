package fr.tac.cryptac.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Details(
    @PrimaryKey(autoGenerate = false)
    val cryptoId: Int,
    val name: String,
    val symbol: String,
    val description: String,
    val logo: String,
    val website: String,
    val sourceCode: String,
    val technicalDoc: String,
    val reddit: String,
    val twitter: String
)

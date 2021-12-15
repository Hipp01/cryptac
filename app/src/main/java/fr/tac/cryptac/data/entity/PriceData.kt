package fr.tac.cryptac.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_data")
data class PriceData(
    @PrimaryKey(autoGenerate = false)
    val cryptoId: Int,
    val cmcRank: Int,
    val price: Double,
    val percentChange24h: Float,
    val marketCap: Double,
    val volume24h: Double,
    val circulatingSupply: Double,
    val currency: String
)

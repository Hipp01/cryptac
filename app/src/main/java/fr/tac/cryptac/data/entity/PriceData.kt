package fr.tac.cryptac.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_data")
data class PriceData(
    @PrimaryKey(autoGenerate = false)
    val cryptoId: Int,
    val cmcRank: Int,
    val price: Int,
    val percentChange24h: Float,
    val marketCap: Long,
    val volume24h: Long,
    val circulatingSupply: Long,
    val currency: String
)

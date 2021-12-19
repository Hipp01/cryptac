package fr.tac.cryptac.model

data class CryptoDetails(
    val logo: String,
    val symbol: String,
    val name: String,
    val description: String,
    val price: Double,
    val percentChange24h: Float,
    val volume24h: Double,
    val marketCap: Double,
    val cmcRank: Int,
    val circulatingSupply: Double,
    val website: String?,
    val sourceCode: String?,
    val technicalDoc: String?,
    val twitter: String?,
    val reddit: String?,
)

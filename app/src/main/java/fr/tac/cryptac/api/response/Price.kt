package fr.tac.cryptac.api.response

data class Price(
    val data: Map<String, PriceData>
)

data class PriceData(
    val circulatingSupply: Int,
    val cmcRank: Int,
    val quote: Map<String, PriceDataQuote>
)

data class PriceDataQuote(
    val price: Double,
    val percentChange1h: Float,
    val volume24h: Float,
    val marketCap: Float
)

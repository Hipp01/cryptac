package fr.tac.cryptac.api.response

data class Quote(
    val data: Map<String, QuoteData>,
)

data class QuoteData(
    val cmcRank: Int,
    val circulatingSupply: Double,
    val quote: Map<String, QuoteDataQuote>
)

data class QuoteDataQuote(
    val price: Double,
    val percentChange24h: Float,
    val volume24h: Double,
    val marketCap: Double,
)

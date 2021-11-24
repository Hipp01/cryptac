package fr.tac.cryptac.api.response

data class Listings(
    val data: List<ListingsData>
)

data class ListingsData(
    val name: String,
    val symbol: String,
    val cmcRank: String,
    val quote: Map<String, ListingsDataQuote>
)

data class ListingsDataQuote(
    val price: Double,
    val percentChange1h: Float
)

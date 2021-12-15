package fr.tac.cryptac.api.response

data class Listings(
    val data: List<ListingsData>
)

data class ListingsData(
    val id: Int,
    val cmcRank: Int,
    val name: String,
    val symbol: String,
    val quote: Map<String, ListingsDataQuote>
)

data class ListingsDataQuote(
    val price: Double,
    val percentChange1h: Float
)

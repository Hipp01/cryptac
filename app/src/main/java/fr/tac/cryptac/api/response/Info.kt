package fr.tac.cryptac.api.response

data class Info(
    val data: Map<String, InfoData>
)

data class InfoData(
    val id: Int,
    val name: String,
    val symbol: String,
    val logo: String,
    val description: String,
    val urls: InfoDataUrls
)

data class InfoDataUrls(
    val website: List<String>,
    val twitter: List<String>,
    val technicalDoc: List<String>,
    val sourceCode: List<String>,
    val reddit: List<String>,
)

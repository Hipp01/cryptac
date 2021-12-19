package fr.tac.cryptac.util

const val LOGO_BASE_URL = "https://s2.coinmarketcap.com/static/img/coins/128x128/%d.png"

object CryptoUtils {
    /**
     * Get the logo URL of a given crypto
     * @param cryptoId the ID of the crypto
     */
    fun getLogo(cryptoId: Int) = LOGO_BASE_URL.format(cryptoId)
}

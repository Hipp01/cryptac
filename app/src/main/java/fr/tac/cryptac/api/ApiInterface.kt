package fr.tac.cryptac.api

import fr.tac.cryptac.api.response.Info
import fr.tac.cryptac.api.response.Listings
import fr.tac.cryptac.shared.AppConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines all API routes used by the app
 */
interface ApiInterface {
    /**
     * Get the information about a specific cryptocurrency
     * @param symbol the crypto symbol (BTC, ETH...)
     */
    @GET("cryptocurrency/info")
    fun getInfo(@Query("symbol") symbol: String): Call<Info>

    /**
     * Get the list of the best cryptocurrencies
     * @param limit (optional) the number of cryptocurrencies to get
     */
    @GET("cryptocurrency/listings/latest?convert=" + AppConfig.CURRENCY)
    fun getListings(@Query("limit") limit: Short = 50): Call<Listings>
}

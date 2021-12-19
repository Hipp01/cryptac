package fr.tac.cryptac.api

import fr.tac.cryptac.api.response.Info
import fr.tac.cryptac.api.response.Listings
import fr.tac.cryptac.api.response.Quote
import fr.tac.cryptac.shared.CURRENCY
import io.reactivex.rxjava3.core.Single
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
    fun getInfo(@Query("symbol") symbol: String): Single<Info>

    /**
     * Get the list of the best cryptocurrencies
     * @param limit (optional) the number of cryptocurrencies to get
     */
    @GET("cryptocurrency/listings/latest?convert=$CURRENCY")
    fun getListings(@Query("limit") limit: Short = 50): Single<Listings>

    /**
     * Get the price of a given cryptocurrency
     * @param symbol the crypto symbol (BTC, ETH...)
     */
    @GET("cryptocurrency/quotes/latest?convert=$CURRENCY")
    fun getPriceData(@Query("symbol") symbol: String): Single<Quote>
}

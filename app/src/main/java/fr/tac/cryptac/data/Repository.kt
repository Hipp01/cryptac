package fr.tac.cryptac.data

import android.app.Application
import android.util.Log
import fr.tac.cryptac.api.ApiClient
import fr.tac.cryptac.data.entity.Details
import fr.tac.cryptac.data.entity.Favorite
import fr.tac.cryptac.data.room.AppDatabase
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.model.CryptoDetails
import fr.tac.cryptac.shared.CURRENCY
import fr.tac.cryptac.util.CryptoUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

private val TAG = Repository::class.simpleName

/**
 * Repository to get the application data, either locally or remotely.
 */
class Repository(application: Application) {

    /**
     * Retrofit api client instance
     */
    private val retrofit = ApiClient.create()

    /**
     * Initialize the database when it is first accessed
     */
    private val database by lazy { AppDatabase.getInstance(application) }

    /**
     * Fetch the list of the top cryptos from the API and retrieve the list of favorites from
     * the database in order to determine the favorites cryptos
     * @return a Single containing the list of cryptos
     */
    fun getCryptoList(): Single<List<CryptoBasic>> = retrofit.getListings()
        .zipWith(database.favoriteDao().getAll()) { listings, favorites ->
            listings.data.map { crypto ->
                val marketData = crypto.quote[CURRENCY] ?: error("empty market data")

                CryptoBasic(
                    crypto.id,
                    crypto.cmcRank,
                    crypto.name,
                    crypto.symbol,
                    CryptoUtils.getLogo(crypto.id),
                    marketData.price,
                    marketData.percentChange1h,
                    favorites.contains(Favorite(crypto.symbol))
                )
            }
        }

    /**
     * Get the details about a crypto currency. Try to get the details locally first. If no data
     * exists in local database, fetch details remotely from the API, and then save the result
     * locally for later reuse.
     * As price data constantly changes, it is always fetched from the API and never saved locally.
     * @param symbol the crypto symbol (BTC, ETH...)
     * @return a Single of the crypto details
     */
    fun getCryptoDetails(symbol: String): Single<CryptoDetails> = database.detailsDao()
        .getDetails(symbol)
        .doOnSuccess { Log.d(TAG, "Found local data for $symbol") }
        .onErrorResumeNext {
            Log.d(TAG, "Fetching remote data for $symbol")
            retrofit.getInfo(symbol)
                .map {
                    val info = it.data[symbol] ?: error("empty data")

                    val details = Details(
                        info.id,
                        info.name,
                        info.symbol,
                        info.description,
                        info.logo,
                        safeGetFirst(info.urls.website),
                        safeGetFirst(info.urls.sourceCode),
                        safeGetFirst(info.urls.technicalDoc),
                        safeGetFirst(info.urls.twitter),
                        safeGetFirst(info.urls.reddit),
                    )

                    database.detailsDao().saveDetails(details)
                    Log.d(TAG, "Successfully saved local data for $symbol")

                    details
                }
        }
        .zipWith(retrofit.getPriceData(symbol)) { details, latestQuote ->
            val quote = latestQuote.data[symbol] ?: error("no quote data")
            val priceData = quote.quote[CURRENCY] ?: error("no price data")

            CryptoDetails(
                CryptoUtils.getLogo(details.cryptoId),
                details.symbol,
                details.name,
                details.description,
                priceData.price,
                priceData.percentChange1h,
                priceData.volume24h,
                priceData.marketCap,
                quote.cmcRank,
                quote.circulatingSupply,
                details.website,
                details.sourceCode,
                details.technicalDoc,
                details.twitter,
                details.reddit,
            )
        }

    /**
     * Set the favorite state of a given crypto to the provided value
     * @param symbol the crypto symbol (BTC, ETH...)
     * @param value true to save the crypto as a favorite, false to remove it
     * @return a completable
     */
    fun setFavorite(symbol: String, value: Boolean): Completable = if (value) {
        database.favoriteDao().add(Favorite(symbol))
    } else {
        database.favoriteDao().remove(Favorite(symbol))
    }

    companion object {
        /**
         * Safely get the first element of an array of strings. If the element does not exist,
         * return null.
         * @param array the array
         * @return the first string of the array or null
         */
        private fun safeGetFirst(array: List<String>) = array.getOrElse(0) { null }
    }
}

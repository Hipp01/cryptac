package fr.tac.cryptac.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.tac.cryptac.api.ApiClient
import fr.tac.cryptac.api.response.Listings
import fr.tac.cryptac.data.room.AppDatabase
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.shared.AppConfig
import fr.tac.cryptac.util.CryptoUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val TAG = Repository::class.simpleName

/**
 * Application repository
 */
class Repository(application: Application) {
    /**
     * Retrofit api client instance
     */
    private val retrofit = ApiClient.create()

    /**
     * Initialize the database when the constructor is called
     */
    private val database by lazy {
        AppDatabase.getInstance(application)
    }

    /**
     * The list of top cryptos
     */
    private val cryptoList: MutableLiveData<List<CryptoBasic>> = MutableLiveData()

    /**
     * Fetch the list of the top cryptos from the API
     */
    fun getCryptoList(): LiveData<List<CryptoBasic>> {

        retrofit.getListings().enqueue(object : Callback<Listings> {
            override fun onResponse(call: Call<Listings>, response: Response<Listings>) {
                val listings = response.body()?.data ?: emptyList()
                val cryptos = listings.map {
                    val marketData = it.quote[AppConfig.CURRENCY] ?: error("empty market data")

                    CryptoBasic(
                        it.id,
                        it.cmcRank,
                        it.name,
                        it.symbol,
                        CryptoUtils.getLogo(it.id),
                        marketData.price,
                        marketData.percentChange1h,
                        false
                    )
                }

                cryptoList.postValue(cryptos)
            }

            override fun onFailure(call: Call<Listings>, t: Throwable) {
                Log.e(TAG, "could not get listings: $t")
            }
        })

        return cryptoList
    }

    /**
     * Execute a database instruction in the database writer executor
     */
    private fun execute(function: () -> Unit) {
        AppDatabase.databaseWriteExecutor.execute(function)
    }
}

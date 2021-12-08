package fr.tac.cryptac.data.room.repositories

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import fr.tac.cryptac.data.entity.PriceData

class PriceDataRepository(application: Application) : BaseRepository(application) {
    /**
     * Initialize the DAO when the constructor is called
     */
    private val priceDataDao by lazy {
        database.priceDataDao()
    }

    /**
     * Get the price data of a given cryptocurrency
     * @param id the CMC id of the cryptocurrency
     */
    fun getPriceData(id: Int) = priceDataDao.getPriceData(id)

    /**
     * Save the price data for the given cryptocurrency. If some data
     * already exists, override with new data.
     * @param priceData the price data to save
     */
    fun savePriceData(priceData: PriceData) = execute {
        try {
            priceDataDao.savePriceData(priceData)
        } catch (e: SQLiteConstraintException) {
            priceDataDao.updatePriceData(priceData)
        }
    }
}

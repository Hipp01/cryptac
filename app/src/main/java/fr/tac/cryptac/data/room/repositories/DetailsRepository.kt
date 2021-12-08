package fr.tac.cryptac.data.room.repositories

import android.app.Application
import fr.tac.cryptac.data.entity.Details

class DetailsRepository(application: Application) : BaseRepository(application) {
    /**
     * Initialize the DAO when the constructor is called
     */
    private val detailsDao by lazy {
        database.detailsDao()
    }

    /**
     * Get the details of a cryptocurrency
     */
    fun getDetails(cryptoId: Int) = detailsDao.getDetails(cryptoId)

    /**
     * Save the details of a cryptocurrency
     */
    fun saveDetails(details: Details) = execute {
        detailsDao.saveDetails(details)
    }
}

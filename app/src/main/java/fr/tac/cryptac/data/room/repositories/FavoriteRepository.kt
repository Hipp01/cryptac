package fr.tac.cryptac.data.room.repositories

import android.app.Application
import fr.tac.cryptac.data.entity.Favorite

class FavoriteRepository(application: Application) : BaseRepository(application) {
    /**
     * Initialize the DAO when the constructor is called
     */
    private val favoriteDao by lazy {
        database.favoriteDao()
    }

    /**
     * Get all the favorites
     */
    fun getAll() = favoriteDao.getAll()

    /**
     * Insert a new favorite from its ID
     */
    fun insert(id: Int) = execute {
        favoriteDao.add(Favorite(id))
    }

    /**
     * Remove a favorite from its ID
     */
    fun remove(id: Int) = execute {
        favoriteDao.remove(Favorite(id))
    }
}

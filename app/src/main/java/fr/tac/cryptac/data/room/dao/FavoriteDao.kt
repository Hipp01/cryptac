package fr.tac.cryptac.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fr.tac.cryptac.data.entity.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): LiveData<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(favorite: Favorite): Long

    @Delete
    fun remove(favorite: Favorite)
}

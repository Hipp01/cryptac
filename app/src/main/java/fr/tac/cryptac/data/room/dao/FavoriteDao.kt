package fr.tac.cryptac.data.room.dao

import androidx.room.*
import fr.tac.cryptac.data.entity.Favorite
import io.reactivex.Observable

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): Observable<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(favorite: Favorite): Long

    @Delete
    fun remove(favorite: Favorite)
}

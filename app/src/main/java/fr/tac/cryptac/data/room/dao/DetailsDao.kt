package fr.tac.cryptac.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.tac.cryptac.data.entity.Details
import io.reactivex.rxjava3.core.Single

@Dao
interface DetailsDao {
    @Query("SELECT * FROM details WHERE symbol = :symbol")
    fun getDetails(symbol: String): Single<Details>

    @Insert
    fun saveDetails(details: Details)
}

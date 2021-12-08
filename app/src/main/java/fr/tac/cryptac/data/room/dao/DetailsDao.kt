package fr.tac.cryptac.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.tac.cryptac.data.entity.Details
import io.reactivex.Observable

@Dao
interface DetailsDao {
    @Query("SELECT * FROM details WHERE cryptoId = :cryptoId")
    fun getDetails(cryptoId: Int): Observable<Details>

    @Insert
    fun saveDetails(details: Details)
}

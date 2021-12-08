package fr.tac.cryptac.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.tac.cryptac.data.entity.PriceData
import io.reactivex.Observable

@Dao
interface PriceDataDao {
    @Query("SELECT * FROM price_data WHERE cryptoId = :cryptoId")
    fun getPriceData(cryptoId: Int): Observable<PriceData>

    @Insert
    fun savePriceData(priceData: PriceData)

    @Update
    fun updatePriceData(priceData: PriceData)
}

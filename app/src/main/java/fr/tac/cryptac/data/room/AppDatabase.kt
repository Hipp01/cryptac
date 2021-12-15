package fr.tac.cryptac.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.tac.cryptac.data.entity.Details
import fr.tac.cryptac.data.entity.Favorite
import fr.tac.cryptac.data.entity.PriceData
import fr.tac.cryptac.data.room.dao.DetailsDao
import fr.tac.cryptac.data.room.dao.FavoriteDao
import fr.tac.cryptac.data.room.dao.PriceDataDao
import fr.tac.cryptac.shared.AppConfig
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Details::class, Favorite::class, PriceData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detailsDao(): DetailsDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun priceDataDao(): PriceDataDao

    companion object {
        /**
         * Singleton instance of the database
         */
        private var instance: AppDatabase? = null

        /**
         * Write executor to update the database in a separate thread
         */
        val databaseWriteExecutor: ExecutorService = Executors.newSingleThreadExecutor()

        /**
         * Get an instance of the database
         */
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context, AppDatabase::class.java, AppConfig.DATABASE_NAME
                ).build()
            }
            return instance as AppDatabase
        }
    }
}

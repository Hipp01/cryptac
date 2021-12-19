package fr.tac.cryptac.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.tac.cryptac.data.entity.Details
import fr.tac.cryptac.data.entity.Favorite
import fr.tac.cryptac.data.room.dao.DetailsDao
import fr.tac.cryptac.data.room.dao.FavoriteDao
import fr.tac.cryptac.shared.DATABASE_NAME

@Database(entities = [Details::class, Favorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detailsDao(): DetailsDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        /**
         * Singleton instance of the database
         */
        private var instance: AppDatabase? = null

        /**
         * Get an instance of the database
         */
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()
            }
            return instance as AppDatabase
        }
    }
}

package fr.tac.cryptac.data.room.repositories

import android.app.Application
import fr.tac.cryptac.data.room.AppDatabase

/**
 * Base repository that has to be extended by specific ones
 */
abstract class BaseRepository(application: Application) {
    /**
     * Initialize the database when the constructor is called
     */
    protected val database by lazy {
        AppDatabase.getInstance(application)
    }

    /**
     * Execute a database instruction in the database writer executor
     */
    protected fun execute(function: () -> Unit) {
        AppDatabase.databaseWriteExecutor.execute(function)
    }
}

package fr.tac.cryptac.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fr.tac.cryptac.data.Repository
import fr.tac.cryptac.model.CryptoBasic
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * ViewModel associated with the main activity
 */
class MainViewModel(private val application: Application) : ViewModel() {

    /**
     * Send an event each time a crypto has been added/removed from favorites
     */
    val favoritesListener: PublishSubject<CryptoBasic> = PublishSubject.create()

    /**
     * Initialize the repository on first call
     */
    private val repository by lazy { Repository(application) }

    /**
     * The list of the top cryptos
     */
    fun getCryptoList() = repository.getCryptoList()

    /**
     * Set the favorite
     */
    fun setFavorite(symbol: String, value: Boolean) = repository.setFavorite(symbol, value)
}

package fr.tac.cryptac.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fr.tac.cryptac.data.Repository

/**
 * ViewModel associated with the main activity
 */
class MainViewModel(private val application: Application) : ViewModel() {

    /**
     * Initialize the repository on first call
     */
    private val repository by lazy { Repository(application) }

    /**
     * The list of the top cryptos
     */
    val cryptoList = repository.getCryptoList()
}

package fr.tac.cryptac.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fr.tac.cryptac.data.Repository

class DetailsViewModel(private val application: Application) : ViewModel() {

    /**
     * Initialize the repository on first call
     */
    private val repository by lazy { Repository(application) }

    /**
     * Get the details of a given crypto
     * @param symbol the crypto symbol (BTC, ETH...)
     * @return the crypto details
     */
    fun getCryptoDetails(symbol: String) = repository.getCryptoDetails(symbol)
}

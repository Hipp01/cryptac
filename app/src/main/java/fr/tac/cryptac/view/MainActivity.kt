package fr.tac.cryptac.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.adapter.CryptoAdapter
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    /**
     * Create the instances when they are first called
     * @see {https://kotlinlang.org/docs/delegated-properties.html#lazy-properties}
     */
    private val viewModel: MainViewModel by lazy { MainViewModel(application) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.cryptos) }

    private lateinit var disposable: Disposable

    /**
     * Load the list of the top cryptos
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        disposable = viewModel.cryptoList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { cryptoList -> recyclerView.adapter = CryptoAdapter(cryptoList, viewModel) }
    }

    /**
     * Dispose all running observables
     */
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}

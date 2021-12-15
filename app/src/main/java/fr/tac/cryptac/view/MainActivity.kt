package fr.tac.cryptac.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.adapter.CryptoAdapter
import fr.tac.cryptac.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    /**
     * Create the instances when they are first called
     * @see {https://kotlinlang.org/docs/delegated-properties.html#lazy-properties}
     */
    private val viewModel: MainViewModel by lazy { MainViewModel(application) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.cryptos) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = CryptoAdapter(viewModel.cryptoList.value ?: emptyList())

        viewModel.cryptoList.observe(this, { recyclerView.adapter = CryptoAdapter(it) })
    }
}

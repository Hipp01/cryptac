package fr.tac.cryptac.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.tac.cryptac.R
import fr.tac.cryptac.databinding.ActivityDetailsBinding
import fr.tac.cryptac.viewmodel.DetailsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

const val SYMBOL = "SYMBOL"

class DetailsActivity : AppCompatActivity() {

    /**
     * Create instances when they are first called
     * @see {https://kotlinlang.org/docs/delegated-properties.html#lazy-properties}
     */
    private val viewModel by lazy { DetailsViewModel(application) }
    private val binding by lazy { ActivityDetailsBinding.inflate(layoutInflater) }

    private lateinit var disposable: Disposable

    /**
     * Load the crypto details and update the model once the data is loaded
     */
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_loading)

        val symbol = intent.getStringExtra(SYMBOL) ?: error("missing symbol extra")

        disposable = viewModel.getCryptoDetails(symbol)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { crypto ->
                binding.model = crypto
                binding.website.setOnClickListener { openLink(crypto.website) }
                binding.github.setOnClickListener { openLink(crypto.sourceCode) }
                binding.documentation.setOnClickListener { openLink(crypto.technicalDoc) }
                binding.twitter.setOnClickListener { openLink(crypto.twitter) }
                binding.reddit.setOnClickListener { openLink(crypto.reddit) }
                setContentView(binding.root)
            }
    }

    /**
     * Dispose from the running observables when the activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    /**
     * Create an intent to open a link to the browser
     * @param link the link to open
     */
    private fun openLink(link: String?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}

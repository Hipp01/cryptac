package fr.tac.cryptac.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import fr.tac.cryptac.R
import fr.tac.cryptac.databinding.ActivityDetailsBinding
import fr.tac.cryptac.model.CryptoDetails
import fr.tac.cryptac.viewmodel.DetailsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

private val TAG = DetailsActivity::class.simpleName

private const val DETAILS = "DETAILS"
const val SYMBOL = "SYMBOL"

class DetailsActivity : AppCompatActivity() {

    /**
     * Create instances when they are first called
     * @see {https://kotlinlang.org/docs/delegated-properties.html#lazy-properties}
     */
    private val viewModel by lazy { DetailsViewModel(application) }
    private val binding by lazy { ActivityDetailsBinding.inflate(layoutInflater) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val retry: Button by lazy { binding.error.findViewById(R.id.retry) }
    private val swipeContainer: SwipeRefreshLayout by lazy { findViewById(R.id.swipe_container) }


    private var disposable: Disposable? = null

    /**
     * Setup the activity
     */
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(binding.root)
        enableToolbar()
        val symbol = intent.getStringExtra(SYMBOL) ?: error("missing symbol extra")
        val savedCrypto = bundle?.getString(DETAILS)
        swipeContainer.setOnRefreshListener { loadDetails(symbol) }
        if (savedCrypto != null) {
            Log.i(TAG, "Using details from bundle")
            val crypto = Gson().fromJson(savedCrypto, CryptoDetails::class.java)
            detailsLoaded(crypto)
        } else {
            Log.i(TAG, "Fetch details from repository")
            loadDetails(symbol)
        }

        retry.setOnClickListener { loadDetails(symbol) }
    }

    /**
     * Load the crypto details from the repository and update the model once the data is loaded.
     * Display an error if the loading failed.
     * @param symbol the crypto symbol
     */
    private fun loadDetails(symbol: String) {
        binding.error.visibility = View.GONE
        swipeContainer.isRefreshing = true

        disposable = viewModel.getCryptoDetails(symbol)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ crypto ->
                detailsLoaded(crypto)
            }, {
                Log.e(TAG, "Could not get crypto details: $it")
                binding.error.visibility = View.VISIBLE
                binding.list.visibility = View.GONE
                swipeContainer.isRefreshing = false
            })
    }

    /**
     * Callback when the details are loaded to set up the links listeners and to display
     * the activity details.
     * @param crypto the crypto details
     */
    private fun detailsLoaded(crypto: CryptoDetails) {
        supportActionBar?.title = crypto.name
        binding.model = crypto
        binding.website.root.setOnClickListener { openLink(crypto.website) }
        binding.github.root.setOnClickListener { openLink(crypto.sourceCode) }
        binding.documentation.root.setOnClickListener { openLink(crypto.technicalDoc) }
        binding.twitter.root.setOnClickListener { openLink(crypto.twitter) }
        binding.reddit.root.setOnClickListener { openLink(crypto.reddit) }
        binding.list.visibility = View.VISIBLE
        swipeContainer.isRefreshing = false
    }

    /**
     * Enable the toolbar
     */
    private fun enableToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * When the toolbar back arrow is pressed, go back to the main activity
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Inflate the toolbar in the menu
     * @param menu the menu where to inflate the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Save the state of the application for later reuse (the details of the crypto)
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DETAILS, Gson().toJson(binding.model))
    }

    /**
     * Dispose from the running observables when the activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    /**
     * Create an intent to open a link to the browser
     * @param link the link to open
     */
    private fun openLink(link: String?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}

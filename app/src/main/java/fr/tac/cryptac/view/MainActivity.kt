package fr.tac.cryptac.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.tac.cryptac.R
import fr.tac.cryptac.adapter.CryptoBaseAdapter
import fr.tac.cryptac.enums.Alpha
import fr.tac.cryptac.enums.Layout
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.util.GsonUtils
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

private val TAG = MainActivity::class.simpleName

// Bundle keys
private const val CRYPTO_LIST = "CRYPTO_LIST"
private const val LAYOUT = "LAYOUT"
private const val ONLY_FAVORITES = "ONLY_FAVORITES"

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    /**
     * Create the instances when they are first called
     * @see {https://kotlinlang.org/docs/delegated-properties.html#lazy-properties}
     */
    private val viewModel: MainViewModel by lazy { MainViewModel(application) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.cryptos) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val spinner: ProgressBar by lazy { findViewById(R.id.spinner) }
    private val error: ConstraintLayout by lazy { findViewById(R.id.error) }
    private val retry: Button by lazy { error.findViewById(R.id.retry) }
    private val layoutAnimation by lazy { loadLayoutAnimation(this, R.anim.layout_animation) }
    private val swipeContainer: SwipeRefreshLayout by lazy { findViewById(R.id.swipe_container) }

    // Toolbar menu items
    private lateinit var actionGrid: MenuItem
    private lateinit var actionList: MenuItem
    private lateinit var actionFavorites: MenuItem

    // Stored in bundle
    private lateinit var cryptoList: List<CryptoBasic>
    private var layout = Layout.LIST
    private var onlyFavorites: Boolean = false

    // Others
    private lateinit var adapter: CryptoBaseAdapter<out ViewDataBinding>
    private var fetchCryptoList: Disposable? = null
    private var favoritesListener: Disposable? = null

    // Setup the activity (set the according layout, set the toolbar, load the crypto list).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        readBundle(savedInstanceState)
        swipeContainer.setOnRefreshListener { loadCryptoList() }
        retry.setOnClickListener { loadCryptoList() }
    }

    // Read potentially saved values from the bundle.
    private fun readBundle(bundle: Bundle?) {
        val savedList = bundle?.getString(CRYPTO_LIST)
        val savedLayout = bundle?.getString(LAYOUT)
        val savedOnlyFavorites = bundle?.getBoolean(ONLY_FAVORITES)

        if (savedList != null) {
            cryptoList = GsonUtils.fromJson(savedList)
        }

        if (savedLayout != null) {
            layout = Layout.valueOf(GsonUtils.fromJson(savedLayout))
        }

        if (savedOnlyFavorites != null) {
            onlyFavorites = savedOnlyFavorites
        }
    }

    // Render the crypto list to the screen. If it doesn't exist locally, fetch it from the repository.
    private fun displayCryptoList() {
        if (::cryptoList.isInitialized) {
            cryptoListLoaded()
        } else {
            loadCryptoList()
        }
    }

    // Load the list of the top cryptos. Display an error if the loading failed.
    private fun loadCryptoList() {
        spinner.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        error.visibility = View.GONE

        fetchCryptoList = viewModel.cryptoList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                cryptoList = result
                cryptoListLoaded()
            }, {
                Log.e(TAG, "Could not get crypto list: $it")
                spinner.visibility = View.GONE
                error.visibility = View.VISIBLE
            })
    }

    private fun cryptoListLoaded() {
        setLayout(layout)
        displayToolbarItems()
        recyclerView.visibility = View.VISIBLE
        spinner.visibility = View.GONE
        swipeContainer.isRefreshing = false

        // Listen for favorites list changes
        if (favoritesListener == null) {
            favoritesListener = viewModel.favoritesListener.subscribe { updatedCrypto ->
                if (onlyFavorites && !updatedCrypto.isFavorite) {
                    adapter.submitList(getCurrentList())
                }
            }
        }
    }

    /**
     * Update the current layout for the RecyclerView to display the cryptos either as list
     * or as grid.
     * @param newLayout the new layout to set (grid or list)
     * @see Layout
     */
    private fun setLayout(newLayout: Layout) {
        layout = newLayout
        adapter = layout.adapter.constructors.first().call(viewModel, this)
        adapter.submitList(getCurrentList())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = adapter.getLayoutManager()
        recyclerView.layoutAnimation = layoutAnimation

        when (layout) {
            Layout.GRID -> {
                enableItem(actionGrid)
                disableItem(actionList)
            }
            Layout.LIST -> {
                enableItem(actionList)
                disableItem(actionGrid)
            }
        }
    }

    /**
     * Disable a menu item of the toolbar
     * @param menuItem the menu item to disable
     */
    private fun disableItem(menuItem: MenuItem) {
        menuItem.isEnabled = true
        menuItem.icon.alpha = Alpha.TRANSPARENT
    }

    /**
     * Enable a menu item of the toolbar
     * @param menuItem the menu item to enable
     */
    private fun enableItem(menuItem: MenuItem) {
        menuItem.isEnabled = false
        menuItem.icon.alpha = Alpha.OPAQUE
    }

    // When the crypto list is loaded, display the toolbar menu items.
    private fun displayToolbarItems() {
        actionFavorites.icon = getFavoritesIcon()
        actionGrid.isVisible = true
        actionList.isVisible = true
        actionFavorites.isVisible = true
    }

    /**
     * Inflate the toolbar in the menu
     * @param menu the menu where to inflate the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar, menu)
        actionGrid = menu.findItem(R.id.action_grid)
        actionList = menu.findItem(R.id.action_list)
        actionFavorites = menu.findItem(R.id.action_favorites)
        displayCryptoList()
        return result
    }

    /**
     * Setup click listener for each item of the toolbar
     * @param item the clicked item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list -> setLayout(Layout.LIST)
            R.id.action_grid -> setLayout(Layout.GRID)
            R.id.action_favorites -> toggleFavorites()
        }
        return super.onOptionsItemSelected(item)
    }

    // Toggle the favorites menu item to either show the full list of crypto or only the favorites.
    private fun toggleFavorites() {
        onlyFavorites = !onlyFavorites
        actionFavorites.isChecked = onlyFavorites
        actionFavorites.icon = getFavoritesIcon()
        adapter.submitList(getCurrentList()) { recyclerView.scrollToPosition(0) }
    }

    // Get the current crypto list (all the list or just the favorites)
    private fun getCurrentList(): List<CryptoBasic> = if (onlyFavorites) {
        cryptoList.filter { crypto -> crypto.isFavorite }
    } else {
        cryptoList
    }

    // Get the current icon for the favorite menu item
    private fun getFavoritesIcon() = if (onlyFavorites) {
        getDrawable(this, R.drawable.ic_favorite)
    } else {
        getDrawable(this, R.drawable.ic_favorite_outline)
    }

    /**
     * Save the current state of the application for later reuse (list, display, etc).
     * @param outState the current state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CRYPTO_LIST, GsonUtils.toJson(cryptoList))
        outState.putString(LAYOUT, GsonUtils.toJson(layout.name))
        outState.putBoolean(ONLY_FAVORITES, onlyFavorites)
    }

    // Dispose all running observables
    override fun onDestroy() {
        super.onDestroy()
        fetchCryptoList?.dispose()
        favoritesListener?.dispose()
    }
}

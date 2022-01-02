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
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.adapter.CryptoBaseAdapter
import fr.tac.cryptac.adapter.CryptoGridAdapter
import fr.tac.cryptac.adapter.CryptoListAdapter
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.reflect.KClass

private val TAG = MainActivity::class.simpleName

private const val OPAQUE = 255
private const val TRANSPARENT = 100

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
    private val actionGrid by lazy { toolbar.menu.findItem(R.id.action_grid) }
    private val actionList by lazy { toolbar.menu.findItem(R.id.action_list) }
    private val actionFavorites by lazy { toolbar.menu.findItem(R.id.action_favorites) }
    private val layoutAnimation by lazy { loadLayoutAnimation(this, R.anim.layout_animation) }

    // Others
    private lateinit var adapter: CryptoBaseAdapter<out ViewDataBinding>
    private lateinit var disposable: Disposable
    private lateinit var cryptoList: List<CryptoBasic>

    // Setup the activity (set the according layout, set the toolbar, load the crypto list).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        loadCryptoList()
        retry.setOnClickListener { loadCryptoList() }
    }

    // Load the list of the top cryptos. Display an error if the loading failed.
    private fun loadCryptoList() {
        spinner.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        error.visibility = View.GONE

        disposable = viewModel.cryptoList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                cryptoList = result
                setAdapter(CryptoListAdapter::class)
                displayToolbarItems()
                recyclerView.visibility = View.VISIBLE
                spinner.visibility = View.GONE
            }, {
                Log.e(TAG, "Could not get crypto list: $it")
                spinner.visibility = View.GONE
                error.visibility = View.VISIBLE
            })
    }

    /**
     * Update the current adapter for the RecyclerView to display the cryptos either as list
     * or as grid.
     * @param newAdapter the new adapter to set (has to be an instance of CryptoBaseAdapter)
     */
    private fun setAdapter(newAdapter: KClass<out CryptoBaseAdapter<out ViewDataBinding>>) {
        adapter = newAdapter.constructors.first().call(viewModel, this)
        adapter.submitList(getCurrentList())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = adapter.getLayoutManager()
        recyclerView.layoutAnimation = layoutAnimation

        when (newAdapter) {
            CryptoGridAdapter::class -> {
                enableItem(actionGrid)
                disableItem(actionList)
            }
            CryptoListAdapter::class -> {
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
        menuItem.icon.alpha = TRANSPARENT
    }

    /**
     * Enable a menu item of the toolbar
     * @param menuItem the menu item to enable
     */
    private fun enableItem(menuItem: MenuItem) {
        menuItem.isEnabled = false
        menuItem.icon.alpha = OPAQUE
    }

    /**
     * When the crypto list is loaded, display the toolbar menu items.
     */
    private fun displayToolbarItems() {
        actionFavorites.icon.alpha = TRANSPARENT
        actionGrid.isVisible = true
        actionList.isVisible = true
        actionFavorites.isVisible = true
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
     * Setup item click listener for toolbar
     * @param item the clicked item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list -> setAdapter(CryptoListAdapter::class)
            R.id.action_grid -> setAdapter(CryptoGridAdapter::class)
            R.id.action_favorites -> toggleFavorites()
        }
        return super.onOptionsItemSelected(item)
    }

    // Toggle the favorites menu item to either show the full list of crypto or only the favorites.
    private fun toggleFavorites() {
        actionFavorites.isChecked = !actionFavorites.isChecked
        actionFavorites.icon.alpha = if (actionFavorites.isChecked) OPAQUE else TRANSPARENT
        adapter.submitList(getCurrentList()) { recyclerView.scrollToPosition(0) }
    }

    // Get the current crypto list depending of the favorites item state (checked or not)
    private fun getCurrentList(): List<CryptoBasic> = if (actionFavorites.isChecked) {
        cryptoList.filter { crypto -> crypto.isFavorite }
    } else {
        cryptoList
    }

    // Dispose all running observables
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}

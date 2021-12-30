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
import fr.tac.cryptac.R
import fr.tac.cryptac.adapter.CryptoBaseAdapter
import fr.tac.cryptac.adapter.CryptoGridAdapter
import fr.tac.cryptac.adapter.CryptoListAdapter
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

private val TAG = MainActivity::class.simpleName

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
    private val layoutAnimation by lazy { loadLayoutAnimation(this, R.anim.layout_animation) }

    /**
     * Toolbar items icons
     */
    private val gridIcon by lazy { getDrawable(this, R.drawable.ic_grid) }
    private val listIcon by lazy { getDrawable(this, R.drawable.ic_list) }
    private val gridIconDisabled by lazy { getDrawable(this, R.drawable.ic_grid_disabled) }
    private val listIconDisabled by lazy { getDrawable(this, R.drawable.ic_list_disabled) }

    /**
     * Others
     */
    private lateinit var adapter: CryptoBaseAdapter<out ViewDataBinding>
    private lateinit var disposable: Disposable
    private lateinit var cryptoList: List<CryptoBasic>

    /**
     * Setup the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView.setHasFixedSize(true)
        loadCryptoList()
        retry.setOnClickListener { loadCryptoList() }
    }

    /**
     * Load the list of the top cryptos. Display an error if the loading failed.
     */
    private fun loadCryptoList() {
        spinner.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        error.visibility = View.GONE

        disposable = viewModel.cryptoList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                cryptoList = result
                setListLayout()
                actionGrid.isVisible = true
                actionList.isVisible = true
                recyclerView.visibility = View.VISIBLE
                spinner.visibility = View.GONE
            }, {
                Log.e(TAG, "Could not get crypto list: $it")
                spinner.visibility = View.GONE
                error.visibility = View.VISIBLE
            })
    }

    /**
     * Select the grid layout
     */
    private fun setGridLayout() {
        adapter = CryptoGridAdapter(cryptoList, viewModel, this)
        updateLayout()
        actionGrid.isEnabled = false
        actionList.isEnabled = true
        actionGrid.icon = gridIcon
        actionList.icon = listIconDisabled
    }

    /**
     * Select the list layout
     */
    private fun setListLayout() {
        adapter = CryptoListAdapter(cryptoList, viewModel, this)
        updateLayout()
        actionList.isEnabled = false
        actionGrid.isEnabled = true
        actionGrid.icon = gridIconDisabled
        actionList.icon = listIcon
    }

    /**
     * Update the layout after the adapter has been changed
     */
    private fun updateLayout() {
        recyclerView.layoutManager = adapter.getLayoutManager()
        recyclerView.adapter = adapter
        recyclerView.layoutAnimation = layoutAnimation
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
            R.id.action_list -> setListLayout()
            R.id.action_grid -> setGridLayout()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Dispose all running observables
     */
    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}

package fr.tac.cryptac.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.util.CryptoDiffCallback
import fr.tac.cryptac.view.DetailsActivity
import fr.tac.cryptac.view.SYMBOL
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class CryptoBaseAdapter<T : ViewDataBinding>(
    private val viewModel: MainViewModel,
    private val fragment: Int,
) :
    ListAdapter<CryptoBasic, CryptoBaseAdapter.ViewHolder<T>>(CryptoDiffCallback()) {

    /**
     * Get the layout manager that corresponds to the adapter
     */
    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    /**
     * Setup the fragment binding and associate it to a layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: T = DataBindingUtil
            .inflate(inflater, fragment, parent, false)
        return ViewHolder(binding)
    }

    /**
     * Set the model of the fragment to the corresponding crypto in the local list and set up
     * the click listeners
     * @param holder the ViewHolder
     * @param position the item position in the list
     * @param favorite the favorite element on which to click to save a crypto as favorite
     * @param model the crypto model
     */
    fun onBindViewHolder(
        holder: ViewHolder<T>,
        position: Int,
        favorite: ImageView,
        model: CryptoBasic
    ) {
        setFavoriteClickListener(holder, position, favorite)
        setElementClickListener(holder, model)
        holder.binding.executePendingBindings()
    }

    /**
     * Create the click listener that is triggered when clicking the favorite button of a crypto,
     * in order to toggle the favorite state
     * @param holder the ViewHolder
     * @param position the crypto position in the list
     * @param favorite the favorite element on which the click event will occur
     */
    private fun setFavoriteClickListener(
        holder: ViewHolder<T>,
        position: Int,
        favorite: ImageView
    ) {
        val crypto = getItem(position)
        val context = holder.binding.root.context
        favorite.setOnClickListener {
            viewModel.setFavorite(crypto.symbol, !crypto.isFavorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    crypto.isFavorite = !crypto.isFavorite
                    val resId = if (crypto.isFavorite) {
                        R.string.favorite_added
                    } else {
                        R.string.favorite_removed
                    }
                    makeToast(context, context.getString(resId, crypto.symbol))
                    notifyItemChanged(position, Unit)
                }
        }
    }

    /**
     * Create the click listener that is triggered when clicking the crypto element (either
     * list item or card)
     * @param holder the ViewHolder
     * @param model the crypto model
     */
    private fun setElementClickListener(holder: ViewHolder<T>, model: CryptoBasic) {
        holder.binding.root.setOnClickListener {
            val context = holder.binding.root.context
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(SYMBOL, model.symbol)
            context.startActivity(intent)
        }
    }

    /**
     * Create a toast message
     * @param context the context
     * @param message the message
     */
    private fun makeToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    class ViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)
}

package fr.tac.cryptac.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.databinding.FragmentCardItemBinding
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.view.DetailsActivity
import fr.tac.cryptac.view.SYMBOL
import fr.tac.cryptac.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CryptoAdapter(
    private var cryptoList: List<CryptoBasic>,
    private val viewModel: MainViewModel
) :
    RecyclerView.Adapter<CryptoAdapter.ViewHolder>() {

    /**
     * Setup the fragment binding and associate it to a layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: FragmentCardItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_card_item, parent, false)
        return ViewHolder(binding)
    }

    /**
     * Set the model of the fragment to the corresponding crypto in the local list
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.model = cryptoList[position]
        setFavoriteClickListener(holder, position)
        setElementClickListener(holder)
        holder.binding.executePendingBindings()
    }

    /**
     * Create the click listener that is triggered when clicking the favorite button of a crypto,
     * in order to toggle the favorite state
     * @param holder the ViewHolder
     * @param position the crypto position in the list
     */
    private fun setFavoriteClickListener(holder: ViewHolder, position: Int) {
        val crypto = cryptoList[position]
        val context = holder.binding.root.context
        holder.binding.favorite.setOnClickListener {
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
     */
    private fun setElementClickListener(holder: ViewHolder) {
        holder.binding.root.setOnClickListener {
            val model = holder.binding.model
            val context = holder.binding.root.context
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(SYMBOL, model?.symbol)
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

    override fun getItemCount() = cryptoList.size

    class ViewHolder(val binding: FragmentCardItemBinding) : RecyclerView.ViewHolder(binding.root)
}

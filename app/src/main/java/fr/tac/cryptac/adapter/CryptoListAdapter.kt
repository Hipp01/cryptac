package fr.tac.cryptac.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.databinding.FragmentListItemBinding
import fr.tac.cryptac.model.CryptoBasic
import fr.tac.cryptac.viewmodel.MainViewModel

class CryptoListAdapter(
    private val cryptoList: List<CryptoBasic>,
    viewModel: MainViewModel,
    private val context: Context
) :
    CryptoBaseAdapter<FragmentListItemBinding>(cryptoList, viewModel, R.layout.fragment_list_item) {

    /**
     * Set the model of the fragment to the corresponding crypto in the local list and set up
     * the click listeners
     * @param holder the ViewHolder
     * @param position the item position in the list
     */
    override fun onBindViewHolder(holder: ViewHolder<FragmentListItemBinding>, position: Int) {
        holder.binding.model = cryptoList[position]
        super.onBindViewHolder(holder, position, holder.binding.favorite, cryptoList[position])
    }

    /**
     * Get the layout manager that corresponds to the adapter
     */
    override fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)
}

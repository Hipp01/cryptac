package fr.tac.cryptac.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import fr.tac.cryptac.R
import fr.tac.cryptac.databinding.FragmentCardItemBinding
import fr.tac.cryptac.model.CryptoBasic

class CryptoAdapter(private var cryptoList: List<CryptoBasic>) :
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
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = cryptoList.size

    class ViewHolder(val binding: FragmentCardItemBinding) : RecyclerView.ViewHolder(binding.root)
}

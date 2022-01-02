package fr.tac.cryptac.util

import androidx.recyclerview.widget.DiffUtil
import fr.tac.cryptac.model.CryptoBasic

/**
 * Diff util used by the ListAdapter to determine whether or not two items are identical
 */
class CryptoDiffCallback : DiffUtil.ItemCallback<CryptoBasic>() {
    override fun areItemsTheSame(oldItem: CryptoBasic, newItem: CryptoBasic): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CryptoBasic, newItem: CryptoBasic): Boolean {
        return oldItem == newItem
    }
}

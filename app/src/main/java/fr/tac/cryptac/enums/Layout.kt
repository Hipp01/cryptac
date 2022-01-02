package fr.tac.cryptac.enums

import androidx.databinding.ViewDataBinding
import fr.tac.cryptac.adapter.CryptoBaseAdapter
import fr.tac.cryptac.adapter.CryptoGridAdapter
import fr.tac.cryptac.adapter.CryptoListAdapter
import kotlin.reflect.KClass

/**
 * Represent a layout for the RecyclerView.
 */
enum class Layout(val adapter: KClass<out CryptoBaseAdapter<out ViewDataBinding>>) {
    GRID(CryptoGridAdapter::class),
    LIST(CryptoListAdapter::class)
}

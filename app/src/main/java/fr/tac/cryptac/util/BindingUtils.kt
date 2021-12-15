package fr.tac.cryptac.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

object BindingUtils {
    /**
     * Load glide in an ImageView element from a new attribute
     * @param view the ImageView element
     * @param imageUrl the URL of the image to load
     */
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)
        }
    }

    /**
     * Correctly format the price of a crypto. For instance, it is not necessary to keep too much
     * decimal places when the price is too high. However, as the price lowers, we need to keep
     * track of more decimal places in order for the price to be correctly displayed.
     * @param view the TextView element
     * @param price the price to set
     */
    @JvmStatic
    @BindingAdapter("price")
    fun setPrice(view: TextView, price: Double) {
        val format = NumberFormat.getCurrencyInstance(Locale.FRANCE)
        format.currency = Currency.getInstance(Locale.FRANCE)
        format.maximumFractionDigits = when {
            price > 10 -> 2
            price > 1 -> 3
            price > 0.1 -> 4
            price > 0.01 -> 5
            else -> 6
        }
        view.text = format.format(price)
    }
}

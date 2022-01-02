package fr.tac.cryptac.util

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import fr.tac.cryptac.R
import fr.tac.cryptac.shared.LOCALE
import java.text.NumberFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.roundToInt

const val MAX_DECIMAL_PLACES = 6

object BindingUtils {
    /**
     * Load Glide in an ImageView element from a new attribute. While the image is loading,
     * display a spinner.
     * @param view the ImageView element
     * @param imageUrl the URL of the image to load
     */
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: CardView, imageUrl: String?) {
        val imageView = view.findViewById<ImageView>(R.id.logo)

        if (imageUrl != null) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(imageView)
        }
    }

    /**
     * Set the color tint of an ImageView (typically, an icon).
     * @param view the ImageView element
     * @param color the color tint to set
     */
    @JvmStatic
    @BindingAdapter("tint")
    fun setTint(view: ImageView, @ColorInt color: Int) {
        view.setColorFilter(color)
    }

    /**
     * Correctly format a number, which is, adjust the number of decimal places to display
     * depending on its value
     * @param view the TextView the number will be displayed on
     * @param number the input number
     */
    @JvmStatic
    @BindingAdapter("number")
    fun setNumber(view: TextView, number: Double) {
        val numberFormat = NumberFormat.getNumberInstance(LOCALE)
        numberFormat.maximumFractionDigits = getDecimalPlaces(number)
        view.text = numberFormat.format(number)
    }

    /**
     * Correctly format a price, which is, adjust the number of decimal places to display
     * depending on its value, and add the correct currency symbol
     * @param view the TextView the price will be displayed on
     * @param price the price
     */
    @JvmStatic
    @BindingAdapter("price")
    fun setPrice(view: TextView, price: Double) {
        val numberFormat = NumberFormat.getCurrencyInstance(LOCALE)
        numberFormat.maximumFractionDigits = getDecimalPlaces(price)
        view.text = numberFormat.format(price)
    }

    /**
     * Get the number of decimal places to display (at most 6), depending on the value of the
     * provided number. The smaller the number, the more decimal places are displayed
     * @param number the input number
     * @return the number of decimal places
     */
    private fun getDecimalPlaces(number: Double): Int {
        return min(4 - floor(log10(number)).roundToInt(), MAX_DECIMAL_PLACES)
    }
}

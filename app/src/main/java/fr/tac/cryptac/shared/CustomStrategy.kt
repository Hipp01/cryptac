package fr.tac.cryptac.shared

import com.google.gson.FieldNamingStrategy
import java.lang.reflect.Field

/**
 * Custom strategy to convert a field name into a JSON string property. This strategy converts
 * the field name to lowercase, and also adds underscores between capital letters and numbers.
 *
 * Example: priceChange24h ---> price_change_24h
 */
class CustomStrategy : FieldNamingStrategy {
    /**
     * Translate the field name into a JSON string property
     */
    override fun translateName(f: Field?): String {
        return f?.name?.replace("([A-Z]|\\d+)".toRegex()) { "_${it.value.lowercase()}" } ?: ""
    }
}

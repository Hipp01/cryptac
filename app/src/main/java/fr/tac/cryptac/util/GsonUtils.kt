package fr.tac.cryptac.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gson utils to easily encode objects to JSON and decode them later.
 */
object GsonUtils {
    /**
     * Convert the given object into a JSON string
     * @param src the object to convert
     * @return the JSON string
     */
    fun toJson(src: Any): String = Gson().toJson(src)

    /**
     * Parse an object from a JSON string
     * @param json the JSON string
     * @return the parsed object
     */
    inline fun <reified T> fromJson(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }
}

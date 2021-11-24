package fr.tac.cryptac.api

import com.google.gson.GsonBuilder
import fr.tac.cryptac.api.interceptors.ApiKeyInterceptor
import fr.tac.cryptac.shared.CustomStrategy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client to make API requests
 */
object ApiClient {
    private const val BASE_URL = "https://pro-api.coinmarketcap.com/v1/"

    /**
     * Create a new client implementation for the ApiInterface service interface
     * @return the client implementation
     */
    fun create(): ApiInterface {
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(ApiKeyInterceptor())
        }

        val gson = GsonBuilder()
            .setFieldNamingStrategy(CustomStrategy())
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(ApiInterface::class.java)
    }
}

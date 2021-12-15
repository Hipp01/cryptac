package fr.tac.cryptac.api.interceptors

import fr.tac.cryptac.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the API key in the headers of each request
 */
class ApiKeyInterceptor : Interceptor {

    companion object {
        private const val HEADER_KEY = "X-CMC_PRO_API_KEY"
        private const val HEADER_VALUE = BuildConfig.CMC_API_KEY
    }

    /**
     * Intercept the request and add the API key to the headers
     * @param chain the chain
     * @return the response
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(HEADER_KEY, HEADER_VALUE)
            .build()
        return chain.proceed(request)
    }
}

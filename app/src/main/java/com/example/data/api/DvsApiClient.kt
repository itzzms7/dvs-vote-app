package com.example.data.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

interface DvsApiService {
    @retrofit2.http.GET("api/public/elections/")
    suspend fun getPublicElections(): Response<PublicElectionsResponse>

    @POST("api/voter/authenticate/")
    suspend fun authenticateVoter(
        @Body request: VoterAuthRequest
    ): Response<VoterAuthResponse>

    @POST("api/voter/cast/")
    suspend fun castVote(
        @Body request: VoterCastRequest
    ): Response<VoterCastResponse>
}

/**
 * Thread-safe in-memory Cookie Jar that retains Django sessionid and csrftoken cookies across requests.
 */
class DvsCookieJar : CookieJar {
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val current = cookieStore.getOrPut(host) { mutableListOf() }
        cookies.forEach { newCookie ->
            current.removeAll { it.name == newCookie.name }
            current.add(newCookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }

    fun getCsrfToken(host: String): String? {
        return cookieStore[host]?.find { it.name == "csrftoken" }?.value
    }
}

/**
 * Singleton Retrofit client for the DVS API service.
 */
object DvsApiClient {

    val cookieJar = DvsCookieJar()

    private val csrfInterceptor = Interceptor { chain ->
        val original = chain.request()
        val host = original.url.host
        val csrfToken = cookieJar.getCsrfToken(host)

        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")

        if (csrfToken != null) {
            requestBuilder.header("X-CSRFToken", csrfToken)
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(csrfInterceptor)
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun createService(baseUrl: String = DvsServerConfig.serverBaseUrl): DvsApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(DvsApiService::class.java)
    }
}

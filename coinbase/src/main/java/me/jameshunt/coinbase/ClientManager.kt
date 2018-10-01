package me.jameshunt.coinbase

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.TransactionId
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal class ClientManager(keyValueTool: KeyValueTool) {

    private val authInterceptor = AuthInterceptor(keyValueTool)
    private val reAuthNetworkInterceptor = ReAuthNetworkInterceptor(keyValueTool)

    private val okhttp = OkHttpClient
            .Builder()
            .addInterceptor {
                val newRequest = it
                        .request()
                        .newBuilder()
                        .addHeader("CB-VERSION", "2018-02-20")
                        .build()

                it.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .addInterceptor(reAuthNetworkInterceptor)
            .build()

    private val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .client(okhttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(getRxAdapter())
            .build()

    val client: CoinbaseApi by lazy {
        val client = retrofit.create(CoinbaseApi::class.java)
        reAuthNetworkInterceptor.client = client
        client
    }

    private fun getRxAdapter(): RxJava2CallAdapterFactory {
        return when (Environment.isTesting) {
            false -> RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
            true -> RxJava2CallAdapterFactory.create()
        }
    }
}

class AuthInterceptor(private val keyValueTool: KeyValueTool) : Interceptor {

    private val authHeaderKey: String = "Authorization"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestBuilder: Request.Builder = request.newBuilder()

        val url = request.url().toString()
        if(!url.contains("/oauth/token")) {
            val accessToken = keyValueTool.get(coinbaseAccessToken)

            if (accessToken != null && request.header(authHeaderKey) == null) {
                requestBuilder.addHeader(authHeaderKey, "Bearer $accessToken").build()
            } else {
                println("Using insecure client. User has not logged in yet.")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}

class ReAuthNetworkInterceptor(private val keyValueTool: KeyValueTool) : Interceptor {

    lateinit var client: CoinbaseApi

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val url = response.request().url().toString()

        if (!url.contains("/oauth/token") && response.code() == 401) {
            keyValueTool.get(coinbaseRefreshToken)?.let {
                val tokenResponse = client.getNewTokens(refreshToken = it).execute()

                when(tokenResponse.code()) {
                    200 -> {
                        val tokens = tokenResponse.body()!!
                        keyValueTool.set(coinbaseAccessToken, tokens.accessToken)
                        keyValueTool.set(coinbaseRefreshToken, tokens.refreshToken)
                        return buildNewRequest(chain, tokens.accessToken)
                    }
                    401, 403 -> {
                        keyValueTool.remove(coinbaseAccessToken)
                        keyValueTool.remove(coinbaseRefreshToken)
                    }
                    else -> println("could not get new tokens during refresh: ${tokenResponse.code()}, ${tokenResponse.errorBody()}")
                }
            }
        }

        return response
    }

    private fun buildNewRequest(chain: Interceptor.Chain, accessToken: String): Response {
        val newRequest = chain.request().newBuilder().addHeader("Authorization", accessToken).build()
        return chain.proceed(newRequest)
    }

}

internal const val redirectURI = "huntj88://me.jameshunt.coinly/coinbase"
internal const val scopes = "wallet:accounts:read,wallet:transactions:read"

interface CoinbaseApi {
    //todo look more into "state=SECURE_RANDOM" part of coinbase authorize.
    @POST("/oauth/token" +
            "?grant_type=authorization_code" +
            "&client_id=${CoinbaseKeys.coinbaseClientId}" +
            "&client_secret=${CoinbaseKeys.coinbaseClientSecret}" +
            "&redirect_uri=$redirectURI")
    fun getTokensWithCode(@Query("code") code: String): Single<TokenResponse>

    @POST("/oauth/token" +
            "?grant_type=refresh_token" +
            "&client_id=${CoinbaseKeys.coinbaseClientId}" +
            "&client_secret=${CoinbaseKeys.coinbaseClientSecret}")
    fun getNewTokens(@Query("refresh_token") refreshToken: String): Call<TokenResponse>

    @GET("/v2/accounts/{coinType}/transactions?limit=50&order=asc")
    fun getTransactionsForCoin(
            @Path("coinType") currencyType: CurrencyType,
            @Query("starting_after") recentTransactionID: TransactionId?
    ): Single<CoinbaseTransaction>
}

data class TokenResponse(
        @Json(name = "access_token")
        val accessToken: String,

        @Json(name = "refresh_token")
        val refreshToken: String
)
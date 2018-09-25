package me.jameshunt.coinbase

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

internal class ClientFactory {

    private val okhttp = OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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

    val client: CoinbaseApi = retrofit.create(CoinbaseApi::class.java)

    private fun getRxAdapter(): RxJava2CallAdapterFactory {
        return when (Environment.isTesting) {
            false -> RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
            true -> RxJava2CallAdapterFactory.create()
        }
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
    fun getTokens(@Query("code") code: String): Single<TokenResponse>

    @POST("/oauth/token" +
            "?grant_type=refresh_token" +
            "&client_id=${CoinbaseKeys.coinbaseClientId}" +
            "&client_secret=${CoinbaseKeys.coinbaseClientSecret}")
    fun getAccessToken(@Query("refresh_token") refreshToken: String): Single<TokenResponse>

//    @GET("/v2/accounts/{coinType}/transactions?limit=50&order=asc")
//    fun getTransactionsForCoin(
//            @Path("coinType") coinType: String,
//            @Query("starting_after") recentTransactionID: String?
//    ): Single<CoinbaseTransaction>
}

data class TokenResponse(
        @Json(name = "access_token")
        val accessToken: String,

        @Json(name = "refresh_token")
        val refreshToken: String
)
package me.jameshunt.cryptocompare

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.UnixMilliSeconds
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

internal class ClientFactory {

    private val okhttp = OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    private val moshi = Moshi
            .Builder()
            .add(CurrentPricesRawAdapter())
            .add(HistoricalPriceRawAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/data/")
            .client(okhttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(getRxAdapter())
            .build()

    val client: CryptoCompareApi = retrofit.create(CryptoCompareApi::class.java)

    private fun getRxAdapter(): RxJava2CallAdapterFactory {
        return when (Environment.isTesting) {
            false -> RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
            true -> RxJava2CallAdapterFactory.create()
        }
    }
}

interface CryptoCompareApi {

    @GET("price")
    fun getCurrentPrices(
            @Query("fsym") base: String,
            @Query("tsyms") others: String
    ): Single<CurrentPricesRaw>

    @GET("pricehistorical?markets=coinbase")
    fun getHistoricalPrice(
            @Query("fsym") base: String,
            @Query("tsyms") others: String,
            @Query("ts") time: UnixMilliSeconds
    ): Single<HistoricalPrice>
}
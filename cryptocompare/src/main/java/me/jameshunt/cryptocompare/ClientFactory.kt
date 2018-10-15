package me.jameshunt.cryptocompare

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.jameshunt.base.CurrencyType
import me.jameshunt.cryptocompare.raw.TimeRangeRaw
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
            .add(CurrentPricesAdapter())
            .add(HistoricalPriceAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/data/")
            .client(okhttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .addConverterFactory(ExchangeTypeConverter())
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

//internal class CurrencyTypeListConverter : Converter.Factory() {
//    override fun stringConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<List<CurrencyType>, String>? {
////        val isSameType = type.typeName == (CurrencyType::class.java).canonicalName
////
//        val same = type == List::class.java
//
//        Converter<List<CurrencyType>, String> {  }
//
////        return when (isSameType) {
////            true -> Converter { it.name.toLowerCase() }
////            false -> null
////        }
//    }
//}

//internal class ExchangeTypeConverter : Converter.Factory() {
//    override fun stringConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ExchangeType, String>? {
//
//        val isSameType = (type is Class<*> && type.isEnum)
//
//        return when (isSameType) {
//            true -> Converter { it.name.toLowerCase().capitalize() }
//            false -> null
//        }
//    }
//}

interface CryptoCompareApi {

    @GET("price")
    fun getCurrentPrices(
            @Query("fsym") base: CurrencyType,
            @Query("tsyms") targets: String
    ): Single<CurrentPrices>

    @GET("pricehistorical?markets=coinbase")
    fun getHistoricalPrice(
            @Query("fsym") base: CurrencyType,
            @Query("tsyms") targets: String,
            @Query("ts") time: Long, //seconds, not milli
            //todo made enum
            @Query("e") exchange: String
    ): Single<List<HistoricalPrice>>


    @GET("histoday")
    fun getDailyPrices(
            @Query("fsym") base: CurrencyType,
            @Query("tsym") target: CurrencyType,
            @Query("limit") numDaysAgo: Int
    ): Single<TimeRangeRaw>

    @GET("histohour")
    fun getHourlyPrices(
            @Query("fsym") base: CurrencyType,
            @Query("tsym") target: CurrencyType,
            @Query("limit") numHoursAgo: Int
    ): Single<TimeRangeRaw>

    @GET("histominute")
    fun getMinutePrices(
            @Query("fsym") base: CurrencyType,
            @Query("tsym") target: CurrencyType,
            @Query("limit") numMinAgo: Int
    ): Single<TimeRangeRaw>
}
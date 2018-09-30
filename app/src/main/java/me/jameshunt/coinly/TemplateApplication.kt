package me.jameshunt.coinly

import android.app.Application
import android.os.Looper
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseApplication
import me.jameshunt.base.CurrencyType
import me.jameshunt.base.Repository
import me.jameshunt.repo.Repo
import timber.log.Timber
import javax.inject.Inject

class TemplateApplication : BaseApplication() {

    @Inject
    lateinit var repo: Repository

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        setRxAsyncScheduler()

        this.appComponent = AppComponent.create(this)
        (appComponent as AppComponent).inject(this)

        //testRepo()

    }

    private fun setRxAsyncScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { AndroidSchedulers.from(Looper.getMainLooper(), true) }
    }

    private fun testRepo() {
        // todo: remove
        repo
                .updateTimeRanges(CurrencyType.ETH, CurrencyType.USD)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = { it.printStackTrace() },
                        onComplete = { Timber.i("time ranges updated") }
                )

        repo.updateCurrentPrices(CurrencyType.USD, setOf(CurrencyType.BTC, CurrencyType.ETH))
                .subscribeBy(
                        onError = { it.printStackTrace() },
                        onComplete = { Timber.i("current prices updated") }
                )
    }
}

fun Application.appComponent(): AppComponent {
    return (this as BaseApplication).appComponent as AppComponent
}

package me.jameshunt.coinly

import android.app.Application
import android.os.Looper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseApplication
import me.jameshunt.base.CurrencyType
import me.jameshunt.repo.Repo
import timber.log.Timber
import javax.inject.Inject

class TemplateApplication : BaseApplication() {

    @Inject
    lateinit var repo: Repo

    override fun onCreate() {
        super.onCreate()

        setRxAsyncScheduler()
        this.appComponent = AppComponent.create(this)
        (appComponent as AppComponent).inject(this)

        testRepo()

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
                        onComplete = { Timber.i("wooooo") }
                )
    }
}

fun Application.appComponent(): AppComponent {
    return (this as BaseApplication).appComponent as AppComponent
}

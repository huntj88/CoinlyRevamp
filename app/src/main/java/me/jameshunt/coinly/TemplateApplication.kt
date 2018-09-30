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

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        setRxAsyncScheduler()

        this.appComponent = AppComponent.create(this)
        (appComponent as AppComponent).inject(this)

    }

    private fun setRxAsyncScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { AndroidSchedulers.from(Looper.getMainLooper(), true) }
    }
}

fun Application.appComponent(): AppComponent {
    return (this as BaseApplication).appComponent as AppComponent
}

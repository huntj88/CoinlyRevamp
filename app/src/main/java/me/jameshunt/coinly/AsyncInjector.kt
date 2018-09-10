package me.jameshunt.coinly

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jameshunt.appbase.BaseActivity
import java.util.concurrent.TimeUnit

class AsyncInjector private constructor() {
    companion object {
        fun inject(mainActivity: MainActivity): Completable {
            mainActivity.activityComponent = ActivityComponent.create(mainActivity.application.appComponent())

            return Completable
                    .fromAction { mainActivity.activityComponent().inject(mainActivity) }
                    .subscribeOn(Schedulers.computation())
                    .delay(1, TimeUnit.SECONDS) //to see splash screen. currently dependency graph initializes too fast
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

private fun MainActivity.activityComponent(): ActivityComponent {
    return (this as BaseActivity).activityComponent as ActivityComponent
}
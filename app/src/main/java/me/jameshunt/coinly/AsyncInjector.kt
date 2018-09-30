package me.jameshunt.coinly

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jameshunt.appbase.BaseActivity
import java.util.concurrent.TimeUnit

class AsyncInjector private constructor() {
    companion object {
        fun inject(mainActivity: MainActivity): Completable {
            mainActivity.activityComponent = ActivityComponent.create(mainActivity.application.appComponent(), mainActivity)

            return Completable
                    .fromAction { mainActivity.activityComponent().inject(mainActivity) }
                    .subscribeOn(Schedulers.computation())
        }
    }
}

private fun MainActivity.activityComponent(): ActivityComponent {
    return (this as BaseActivity).activityComponent as ActivityComponent
}
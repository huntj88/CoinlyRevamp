package me.jameshunt.template

import android.app.Application
import android.os.Looper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import me.jameshunt.appbase.BaseApplication

class TemplateApplication: BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        setRxAsyncScheduler()
        this.appComponent = AppComponent.create()
    }

    private fun setRxAsyncScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { AndroidSchedulers.from(Looper.getMainLooper(), true) }
    }
}

fun Application.appComponent(): AppComponent {
    return (this as BaseApplication).appComponent as AppComponent
}
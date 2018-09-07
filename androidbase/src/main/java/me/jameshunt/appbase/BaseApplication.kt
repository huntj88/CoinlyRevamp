package me.jameshunt.appbase

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import me.jameshunt.base.BaseAppComponent
import timber.log.Timber

abstract class BaseApplication: Application() {

    lateinit var appComponent: BaseAndroidAppComponent

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}
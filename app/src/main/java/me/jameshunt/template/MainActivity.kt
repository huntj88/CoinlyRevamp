package me.jameshunt.template

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val visibilityManager = MainVisibilityManager(supportFragmentManager)

        if(savedInstanceState == null) {
            visibilityManager.showSplash()
        }

        AsyncInjector.inject(this).subscribeBy(
                onError = { Timber.e(it) },
                onComplete = {
                    //stop showing splash screen, dependencies ready to go
                    visibilityManager.showPager()
                }
        )
    }

    override fun cleanUp() {

    }
}

package me.jameshunt.coinly

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val visibilityManager = MainVisibilityManager(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        visibilityManager.showCurrent()

        AsyncInjector.inject(this).subscribeBy(
                onError = { Timber.e(it) },
                onComplete = {
                    //stop showing splash screen, dependencies ready to go
                    visibilityManager.showPager()
                }
        )
    }

    override fun onBackPressed() {

        val shouldClose = visibilityManager.onBackPressed()

        if(shouldClose) {
            super.onBackPressed()
        }
    }

    override fun cleanUp() {

    }
}

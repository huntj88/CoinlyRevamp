package me.jameshunt.coinly

import android.content.Intent
import android.net.Uri
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Timber.i(intent.dataString)

        if(!intent.dataString.contains("huntj88://me.jameshunt.coinly")) return

        val deepLink = Uri.parse(intent.dataString)
        when(deepLink.lastPathSegment) {
            "coinbase" -> Timber.i(deepLink.getQueryParameter("code"))
        }
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

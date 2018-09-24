package me.jameshunt.coinly

import android.content.Intent
import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import me.jameshunt.appbase.IntegrationDeepLinkHandler
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val visibilityManager = MainVisibilityManager(supportFragmentManager)

    @Inject
    lateinit var deepLinkHandler: IntegrationDeepLinkHandler

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
        deepLinkHandler.handleIntent(intentString = intent.dataString)
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

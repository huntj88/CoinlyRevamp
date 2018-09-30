package me.jameshunt.coinly

import android.content.Intent
import android.os.Bundle
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import me.jameshunt.appbase.IntegrationDeepLinkHandler
import me.jameshunt.business.UpdateEverythingUseCase
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val visibilityManager = MainVisibilityManager(supportFragmentManager)

    @Inject
    lateinit var updateEverythingUseCase: UpdateEverythingUseCase

    @Inject
    lateinit var deepLinkHandler: IntegrationDeepLinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        visibilityManager.showCurrent()

        AsyncInjector.inject(this)
                .andThen(Completable.defer { updateEverythingUseCase.updateEverything() })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
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

        if (shouldClose) {
            super.onBackPressed()
        }
    }

    override fun cleanUp() {

    }
}

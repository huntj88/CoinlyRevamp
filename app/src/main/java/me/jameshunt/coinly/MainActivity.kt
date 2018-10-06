package me.jameshunt.coinly

import android.content.Intent
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import me.jameshunt.appbase.IntegrationDeepLinkHandler
import me.jameshunt.base.Message
import me.jameshunt.base.addToComposite
import me.jameshunt.base.passMessageThenNext
import me.jameshunt.business.UpdateEverythingUseCase
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val visibilityManager = MainVisibilityManager(supportFragmentManager)

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var updateEverythingUseCase: UpdateEverythingUseCase

    @Inject
    lateinit var deepLinkHandler: IntegrationDeepLinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        visibilityManager.showCurrent()

        AsyncInjector.inject(this)
                .toSingle { Message.Success() as Message }
                .passMessageThenNext(Observable.defer { updateEverythingUseCase.updateEverything() })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { Timber.i(it.toString()) },
                        onError = { it.printStackTrace() },
                        onComplete = {
                            //stop showing splash screen, dependencies ready to go
                            visibilityManager.showPager()
                        }
                ).addToComposite(compositeDisposable)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.dataString?.let {
            deepLinkHandler.handleIntent(intentString = it)
        }
    }

    override fun onBackPressed() {
        val shouldClose = visibilityManager.onBackPressed()

        if (shouldClose) {
            super.onBackPressed()
        }
    }

    override fun cleanUp() {
        compositeDisposable.clear()
    }
}

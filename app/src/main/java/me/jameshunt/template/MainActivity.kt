package me.jameshunt.template

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.appbase.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    companion object {
        var showSplashFragment = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(showSplashFragment) {
            showSplashFragment()
        }

        AsyncInjector.inject(this).subscribeBy(
                onError = { Timber.e(it) },
                onComplete = {
                    //stop showing splash screen, dependencies ready to go
                    if(showSplashFragment) {
                        showPagerFragment()
                    }
                }
        )
    }

    private fun showSplashFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, SplashFragment(), SplashFragment::class.java.simpleName)
                .commit()
    }

    private fun showPagerFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, PagerFragment(), PagerFragment::class.java.simpleName)
                .commit()

        showSplashFragment = false
    }

    override fun cleanUp() {

    }
}

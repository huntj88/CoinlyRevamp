package me.jameshunt.template

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import me.jameshunt.account.LoginFragment
import me.jameshunt.appbase.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showSplashFragment()

        AsyncInjector.inject(this).subscribeBy(
                onError = { Timber.e(it) },
                onComplete = {
                    //stop showing splash screen, dependencies ready to go
                    showPagerFragment()
                }
        )
    }

    private fun showSplashFragment() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, SplashFragment(), SplashFragment::class.java.simpleName)
                .commit()
    }

    private fun showLoginFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment(), LoginFragment::class.java.simpleName)
                .commit()
    }

    private fun showPagerFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, PagerFragment(), PagerFragment::class.java.simpleName)
                .commit()
    }

    override fun cleanUp() {

    }
}
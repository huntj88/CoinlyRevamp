package me.jameshunt.appbase

import android.support.v7.app.AppCompatActivity
import me.jameshunt.base.BaseActivityComponent

abstract class BaseActivity : AppCompatActivity() {

    lateinit var activityComponent: BaseActivityComponent

    private var cleanedUp = false

    abstract fun cleanUp()

    override fun onPause() {
        super.onPause()

        if (isFinishing)
            cleanUpRepoIfNotAlreadyDone()
    }

    override fun onStop() {
        super.onStop()

        if (isChangingConfigurations)
            cleanUpRepoIfNotAlreadyDone()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUpRepoIfNotAlreadyDone()
    }

    private fun cleanUpRepoIfNotAlreadyDone() {
        if (cleanedUp) return

        cleanUp()
        cleanedUp = true
    }
}
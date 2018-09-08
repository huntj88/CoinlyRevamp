package me.jameshunt.appbase

import android.app.Activity
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    lateinit var activityComponent: BaseAndroidActivityComponent

    private var cleanedUp = false

    open fun cleanUp() {}

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

fun Activity.activityComponent(): BaseAndroidActivityComponent {
    return (this as BaseActivity).activityComponent
}
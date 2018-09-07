package me.jameshunt.appbase

import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment() {

    private var cleanedUp = false

    open fun cleanUp() {}

    override fun onPause() {
        super.onPause()

        //todo: check parentFragment maybe?

        if (isRemoving || activity?.isChangingConfigurations == true) {
            cleanUpIfNotAlreadyDone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cleanUpIfNotAlreadyDone()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUpIfNotAlreadyDone()
    }

    private fun cleanUpIfNotAlreadyDone() {
        if (cleanedUp) return

        cleanUp()
        cleanedUp = true
    }
}
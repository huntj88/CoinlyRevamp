package me.jameshunt.account

import timber.log.Timber
import javax.inject.Inject

class LoginDependency @Inject constructor() {

    /**
     * this basically represents the whole data layer and any architecture/patterns, which can be injected into the fragment
     */

    fun doThing() {
        Timber.i("i have an instance of: ")
    }

}
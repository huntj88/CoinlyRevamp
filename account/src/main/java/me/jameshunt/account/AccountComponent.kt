package me.jameshunt.account

import android.support.v4.app.Fragment
import dagger.Component
import me.jameshunt.appbase.BaseActivity
import me.jameshunt.appbase.BaseAndroidActivityComponent
import javax.inject.Scope

@AccountScope
@Component(modules = [], dependencies = [(BaseAndroidActivityComponent::class)])
interface AccountComponent {

    companion object {
        fun create(fragment: Fragment): AccountComponent {
            // i want this to crash if ever null
            val activityComponent = (fragment.activity as BaseActivity?)!!.activityComponent
            return DaggerAccountComponent.builder().baseAndroidActivityComponent(activityComponent).build()
        }
    }

    fun inject(loginFragment: LoginFragment)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountScope
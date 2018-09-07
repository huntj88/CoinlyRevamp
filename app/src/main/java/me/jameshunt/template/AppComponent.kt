package me.jameshunt.template

import dagger.Component
import me.jameshunt.appbase.BaseAndroidAppComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [])
interface AppComponent : BaseAndroidAppComponent {
    //see BaseAppComponent too

    companion object {
        fun create(): AppComponent = DaggerAppComponent
                .builder()
                .build()
    }
}
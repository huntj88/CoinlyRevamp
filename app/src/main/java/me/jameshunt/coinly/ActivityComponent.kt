package me.jameshunt.coinly

import dagger.Component
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.base.ActivityScope

@ActivityScope
@Component(modules = [], dependencies = [(AppComponent::class)])
interface ActivityComponent: BaseAndroidActivityComponent {

    companion object {
        fun create(appComponent: AppComponent): ActivityComponent = DaggerActivityComponent
                .builder()
                .appComponent(appComponent)
                .build()
    }

    fun inject(mainActivity: MainActivity)
}
package me.jameshunt.coinly

import dagger.Component
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.appbase.UrlLauncherModule
import me.jameshunt.base.ActivityScope

@ActivityScope
@Component(modules = [UrlLauncherModule::class], dependencies = [(AppComponent::class)])
interface ActivityComponent: BaseAndroidActivityComponent {

    companion object {
        fun create(appComponent: AppComponent, mainActivity: MainActivity): ActivityComponent = DaggerActivityComponent
                .builder()
                .appComponent(appComponent)
                .urlLauncherModule(UrlLauncherModule(mainActivity))
                .build()
    }

    fun inject(mainActivity: MainActivity)
}
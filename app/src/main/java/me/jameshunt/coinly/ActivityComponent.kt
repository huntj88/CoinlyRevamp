package me.jameshunt.coinly

import dagger.Component
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.appbase.UrlLauncherModule
import me.jameshunt.base.ActivityScope
import me.jameshunt.business.CoinbaseModule

@ActivityScope
@Component(modules = [UrlLauncherModule::class, CoinbaseModule::class], dependencies = [(AppComponent::class)])
interface ActivityComponent: BaseAndroidActivityComponent {

    companion object {
        fun create(appComponent: AppComponent, mainActivity: MainActivity): ActivityComponent = DaggerActivityComponent
                .builder()
                .appComponent(appComponent)
                .urlLauncherModule(UrlLauncherModule(mainActivity))
                .coinbaseModule(CoinbaseModule())
                .build()
    }

    fun inject(mainActivity: MainActivity)
}
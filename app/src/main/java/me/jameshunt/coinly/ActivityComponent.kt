package me.jameshunt.coinly

import dagger.Binds
import dagger.Component
import dagger.Module
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.appbase.UrlLauncherModule
import me.jameshunt.base.ActivityScope
import me.jameshunt.base.SelectedCurrencyUseCase
import me.jameshunt.base.SelectedTimeTypeUseCase
import me.jameshunt.business.CoinbaseModule
import me.jameshunt.business.SelectedCurrencyUseCaseImpl
import me.jameshunt.business.SelectedTimeTypeUseCaseImpl

@ActivityScope
@Component(modules = [UrlLauncherModule::class, CoinbaseModule::class, BusinessModule::class], dependencies = [(AppComponent::class)])
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

@Module
abstract class BusinessModule {
    @Binds
    abstract fun getSelectedCurrencyUseCase(impl: SelectedCurrencyUseCaseImpl): SelectedCurrencyUseCase

    @Binds
    abstract fun getSelectedTimeTypeUseCase(impl: SelectedTimeTypeUseCaseImpl): SelectedTimeTypeUseCase
}

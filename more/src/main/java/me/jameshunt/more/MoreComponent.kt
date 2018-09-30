package me.jameshunt.more

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Component
import dagger.Module
import dagger.Provides
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.appbase.FragmentManagerModule
import me.jameshunt.base.KeyValueTool
import me.jameshunt.coinbase.CoinbaseIntegration
import javax.inject.Scope

@MoreScope
@Component(modules = [FragmentManagerModule::class, MoreFragmentVisibilityModule::class, CoinbaseModule::class], dependencies = [BaseAndroidActivityComponent::class])
interface MoreComponent {
    fun inject(moreFragment: MoreFragment)
    fun inject(moreMenuFragment: MoreMenuFragment)
    fun inject(integrationsFragment: IntegrationsFragment)
    fun inject(coinbaseFragment: CoinbaseFragment)

    fun inject(exampleTemplateFragment: ExampleTemplateFragment)

    companion object {
        fun create(activityComponent: BaseAndroidActivityComponent, childFragmentManager: FragmentManager): MoreComponent = DaggerMoreComponent
                .builder()
                .baseAndroidActivityComponent(activityComponent)
                .fragmentManagerModule(FragmentManagerModule(childFragmentManager))
                .coinbaseModule(CoinbaseModule())
                .build()
    }
}

@Module
class CoinbaseModule {

    @Provides
    fun getCoinbaseIntegration(keyValueTool: KeyValueTool): CoinbaseIntegration = CoinbaseIntegration(keyValueTool)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MoreScope

fun Fragment.moreComponent(): MoreComponent {
    return (this as MoreFragment).moreComponent
}
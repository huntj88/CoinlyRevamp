package me.jameshunt.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Component
import me.jameshunt.appbase.BaseAndroidActivityComponent
import me.jameshunt.appbase.FragmentManagerModule
import javax.inject.Scope

@HomeScope
@Component(modules = [FragmentManagerModule::class], dependencies = [BaseAndroidActivityComponent::class])
interface HomeComponent {

    companion object {
        fun create(activityComponent: BaseAndroidActivityComponent, childFragmentManager: FragmentManager): HomeComponent = DaggerHomeComponent
                .builder()
                .baseAndroidActivityComponent(activityComponent)
                .fragmentManagerModule(FragmentManagerModule(childFragmentManager))
                .build()
    }

    fun inject(homeFragment: HomeFragment)
    fun inject(homeFragment: SummaryFragment)
    fun inject(portfolioFragment: PortfolioFragment)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class HomeScope


fun Fragment.homeComponent(): HomeComponent {
    return (this as HomeFragment).homeComponent
}
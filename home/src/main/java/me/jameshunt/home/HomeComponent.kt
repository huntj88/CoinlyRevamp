package me.jameshunt.home

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Component
import dagger.Module
import dagger.Provides
import me.jameshunt.appbase.BaseActivity
import me.jameshunt.appbase.BaseAndroidActivityComponent
import javax.inject.Scope

@HomeScope
@Component(modules = [HomeModule::class], dependencies = [BaseAndroidActivityComponent::class])
interface HomeComponent {

    companion object {
        fun create(activityComponent: BaseAndroidActivityComponent, childFragmentManager: FragmentManager): HomeComponent = DaggerHomeComponent
                .builder()
                .baseAndroidActivityComponent(activityComponent)
                .homeModule(HomeModule(childFragmentManager))
                .build()
    }

    fun inject(homeFragment: HomeFragment)
    fun inject(homeFragment: SummaryFragment)
    fun inject(portfolioFragment: PortfolioFragment)
}

@Module
class HomeModule(private val childFragmentManager: FragmentManager) {

    @Provides
    internal fun getChildFragmentManager(): FragmentManager = childFragmentManager
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class HomeScope


fun Activity.activityComponent(): BaseAndroidActivityComponent {
    return (this as BaseActivity).activityComponent
}

fun Fragment.homeComponent(): HomeComponent {
    return (this as HomeFragment).homeComponent
}
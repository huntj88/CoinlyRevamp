package me.jameshunt.appbase

import android.content.Context
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import me.jameshunt.base.BaseActivityComponent
import me.jameshunt.base.BaseAppComponent


interface BaseAndroidAppComponent: BaseAppComponent, AndroidAppDependenciesEverywhere
interface BaseAndroidActivityComponent: BaseActivityComponent, AndroidAppDependenciesEverywhere, AndroidActivityDependenciesEverywhere


interface AndroidAppDependenciesEverywhere {
    //these appScopedDependencies that need to be available everywhere
}

interface AndroidActivityDependenciesEverywhere {
    //these ActivityScopedDependencies that need to be available everywhere except as dependencies for @Singleton's

    fun getUrlLauncher(): UrlLauncher
    fun getIntegrationDeepLinkHandler(): IntegrationDeepLinkHandler
}

@Module
class FragmentManagerModule(private val fragmentManager: FragmentManager) {

    @Provides
    fun getFragmentManager(): FragmentManager = fragmentManager
}

@Module
class UrlLauncherModule(private val context: Context) {
    @Provides
    fun getUrlLauncher(): UrlLauncher {
        return UrlLauncherImpl(context)
    }
}
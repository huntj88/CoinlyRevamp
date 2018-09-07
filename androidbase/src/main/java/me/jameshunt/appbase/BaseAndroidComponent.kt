package me.jameshunt.appbase

import me.jameshunt.base.BaseActivityComponent
import me.jameshunt.base.BaseAppComponent


interface BaseAndroidAppComponent: BaseAppComponent, AndroidAppDependenciesEverywhere
interface BaseAndroidActivityComponent: BaseActivityComponent, AndroidAppDependenciesEverywhere, AndroidActivityDependenciesEverywhere


interface AndroidAppDependenciesEverywhere {
    //these appScopedDependencies that need to be available everywhere
}

interface AndroidActivityDependenciesEverywhere {
    //these ActivityScopedDependencies that need to be available everywhere except as dependencies for @Singleton's
}
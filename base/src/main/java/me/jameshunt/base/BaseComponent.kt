package me.jameshunt.base

import javax.inject.Scope


interface BaseAppComponent: AppDependenciesEverywhere
interface BaseActivityComponent: AppDependenciesEverywhere, ActivityDependenciesEverywhere


interface AppDependenciesEverywhere {
    //these appScopedDependencies that need to be available everywhere
    fun getObjectBoxContext(): ObjectBoxContext
    fun getRepo(): Repository
}

interface ActivityDependenciesEverywhere {
    //these ActivityScopedDependencies that need to be available everywhere except as dependencies for @Singleton's
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
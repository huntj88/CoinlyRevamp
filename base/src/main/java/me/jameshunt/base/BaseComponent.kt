package me.jameshunt.base

import javax.inject.Scope


interface BaseAppComponent: AppDependenciesEverywhere
interface BaseActivityComponent: AppDependenciesEverywhere, ActivityDependenciesEverywhere


interface AppDependenciesEverywhere {
    //these appScopedDependencies that need to be available everywhere
    fun getContextWrapper(): ContextWrapper
    fun getRepo(): Repository
}

interface ActivityDependenciesEverywhere {
    //these ActivityScopedDependencies that need to be available everywhere except as dependencies for @Singleton's
    fun getSelectedCurrencyUseCase(): SelectedCurrencyUseCase
    fun getSelectedTimeTypeUseCase(): SelectedTimeTypeUseCase
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
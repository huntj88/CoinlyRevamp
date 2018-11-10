package me.jameshunt.currencyselect

import dagger.Binds
import dagger.Component
import dagger.Module
import me.jameshunt.appbase.BaseAndroidActivityComponent
import javax.inject.Scope

@CurrencySelectScope
@Component(modules = [], dependencies = [BaseAndroidActivityComponent::class])
interface CurrencySelectComponent {

    fun inject(currencySelectDialogFragment: CurrencySelectDialogFragment)

    companion object {
        fun create(activityComponent: BaseAndroidActivityComponent): CurrencySelectComponent = DaggerCurrencySelectComponent
                .builder()
                .baseAndroidActivityComponent(activityComponent)
                .build()
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrencySelectScope

@Module
abstract class CurrencySelectModule {

    @Binds
    abstract fun getCurrencySelectDialogManager(impl: CurrencySelectDialogManagerImpl): CurrencySelectDialogManager
}
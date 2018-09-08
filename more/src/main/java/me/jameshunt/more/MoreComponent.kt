package me.jameshunt.more

import dagger.Component
import me.jameshunt.appbase.BaseAndroidActivityComponent
import javax.inject.Scope

@MoreScope
@Component(modules = [], dependencies = [BaseAndroidActivityComponent::class])
interface MoreComponent {
    fun inject(moreFragment: MoreFragment)

    companion object {
        fun create(activityComponent: BaseAndroidActivityComponent): MoreComponent = DaggerMoreComponent
                .builder()
                .baseAndroidActivityComponent(activityComponent)
                .build()
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MoreScope
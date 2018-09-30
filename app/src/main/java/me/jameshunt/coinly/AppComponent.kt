package me.jameshunt.coinly

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import me.jameshunt.appbase.BaseAndroidAppComponent
import me.jameshunt.base.ObjectBoxContext
import me.jameshunt.base.Repository
import me.jameshunt.repo.Repo
import javax.inject.Singleton

@Singleton
@Component(modules = [ObjectBoxContextModule::class, RepoModule::class])
interface AppComponent : BaseAndroidAppComponent {
    //see BaseAppComponent too

    companion object {
        fun create(context: Context): AppComponent = DaggerAppComponent
                .builder()
                .objectBoxContextModule(ObjectBoxContextModule(context))
                .repoModule(RepoModule())
                .build()
    }

    fun inject(templateApplication: TemplateApplication)
}

@Module
class ObjectBoxContextModule(private val context: Context) {

    @Provides
    fun getObjectBoxContext(): ObjectBoxContext = ObjectBoxContext(context)
}

@Module
class RepoModule {

    @Singleton
    @Provides
    fun getRepo(objectBoxContext: ObjectBoxContext): Repository = Repo(objectBoxContext.context)
}
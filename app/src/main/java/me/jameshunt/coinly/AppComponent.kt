package me.jameshunt.coinly

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import me.jameshunt.appbase.BaseAndroidAppComponent
import me.jameshunt.appbase.KeyValueToolImpl
import me.jameshunt.base.KeyValueTool
import me.jameshunt.base.ContextWrapper
import me.jameshunt.base.Repository
import me.jameshunt.repo.Repo
import javax.inject.Singleton

@Singleton
@Component(modules = [ObjectBoxContextModule::class, RepoModule::class, KeyValueToolModule::class])
interface AppComponent : BaseAndroidAppComponent {
    //see BaseAppComponent too

    companion object {
        fun create(context: Context): AppComponent = DaggerAppComponent
                .builder()
                .objectBoxContextModule(ObjectBoxContextModule(context))
                .repoModule(RepoModule())
                .keyValueToolModule(KeyValueToolModule())
                .build()
    }

    fun inject(templateApplication: TemplateApplication)
}

@Module
class ObjectBoxContextModule(private val context: Context) {

    @Provides
    fun getContextWrapper(): ContextWrapper = ContextWrapper(context)
}

@Module
class KeyValueToolModule {

    @Provides
    fun getKeyValueTool(contextWrapper: ContextWrapper): KeyValueTool {
        val context = contextWrapper.context as Context
        return KeyValueToolImpl(context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE))
    }
}

@Module
class RepoModule {

    @Singleton
    @Provides
    fun getRepo(contextWrapper: ContextWrapper): Repository = Repo(contextWrapper.context)
}


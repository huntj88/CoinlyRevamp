package me.jameshunt.coinly

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import me.jameshunt.appbase.BaseAndroidAppComponent
import me.jameshunt.repo.Repo
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class])
interface AppComponent : BaseAndroidAppComponent {
    //see BaseAppComponent too

    companion object {
        fun create(context: Context): AppComponent = DaggerAppComponent
                .builder()
                .repoModule(RepoModule(context))
                .build()
    }

    fun inject(templateApplication: TemplateApplication)
}


@Module
class RepoModule(private val context: Context) {

    @Singleton
    @Provides
    fun getRepo(): Repo = Repo(context)
}
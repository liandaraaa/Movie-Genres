package com.lianda.movies.utils.di

import android.app.Application
import com.lianda.movies.base.BaseApplication
import com.lianda.movies.utils.di.featuremodule.MovieModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        NetworkModule::class,
        ApiModule::class,
        ViewModelModule::class,
        MovieModule::class,
        AndroidInjectionModule::class
    ]
)

@Singleton
interface AppComponent {
    fun inject(app: BaseApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}
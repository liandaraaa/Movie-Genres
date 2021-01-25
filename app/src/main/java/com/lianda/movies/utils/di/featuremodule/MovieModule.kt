package com.lianda.movies.utils.di.featuremodule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lianda.movies.data.api.remote.MovieApi
import com.lianda.movies.data.repository.MovieRepositoryImpl
import com.lianda.movies.domain.repository.MovieRepository
import com.lianda.movies.domain.usecase.MovieUseCase
import com.lianda.movies.presentation.main.MainActivity
import com.lianda.movies.presentation.movie.MovieDetailActivity
import com.lianda.movies.presentation.movie.MovieListActivity
import com.lianda.movies.presentation.review.ReviewListActivity
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.di.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [MovieModule.ProvideViewModel::class, MovieModule.ProvideRepository::class])
abstract class MovieModule {

    @Module
    class ProvideRepository {
        @Singleton
        @Provides
        fun provideMovieRepository(api: MovieApi): MovieRepository = MovieRepositoryImpl(api)

        @Singleton
        @Provides
        fun provideMovieUseCase(repository: MovieRepository): MovieUseCase =
            MovieViewModel(repository)
    }

    @Module
    class InjectMainViewModel {
        @Provides
        fun provideMovieViewModelToMainActivity(
            factory: ViewModelProvider.Factory,
            target: MainActivity
        ) = ViewModelProvider(target, factory).get(MovieViewModel::class.java)
    }

    @Module
    class InjectMovieListViewModel {
        @Provides
        fun provideMovieViewModelToMovieListActivity(
            factory: ViewModelProvider.Factory,
            target: MovieListActivity
        ) = ViewModelProvider(target, factory).get(MovieViewModel::class.java)
    }

    @Module
    class InjectMovieDetailViewModel {
        @Provides
        fun provideMovieViewModelToMovieDetailActivity(
            factory: ViewModelProvider.Factory,
            target: MovieDetailActivity
        ) = ViewModelProvider(target, factory).get(MovieViewModel::class.java)
    }

    @Module
    class InjectReviewListViewModel {
        @Provides
        fun provideMovieViewModelToReviewListActivity(
            factory: ViewModelProvider.Factory,
            target: ReviewListActivity
        ) = ViewModelProvider(target, factory).get(MovieViewModel::class.java)
    }

    @Module
    class ProvideViewModel {
        @Provides
        @IntoMap
        @ViewModelKey(MovieViewModel::class)
        fun provideMovieViewModel(repository: MovieRepository): ViewModel =
            MovieViewModel(repository)
    }

    @ContributesAndroidInjector(modules = [InjectMainViewModel::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [InjectMovieListViewModel::class])
    abstract fun bindMovieListActivity(): MovieListActivity

    @ContributesAndroidInjector(modules = [InjectMovieDetailViewModel::class])
    abstract fun bindMovieDetailActivity(): MovieDetailActivity

    @ContributesAndroidInjector(modules = [InjectReviewListViewModel::class])
    abstract fun bindReviewListActivity(): ReviewListActivity

}
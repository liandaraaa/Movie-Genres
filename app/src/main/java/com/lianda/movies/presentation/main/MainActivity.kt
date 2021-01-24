package com.lianda.movies.presentation.main

import androidx.recyclerview.widget.GridLayoutManager
import com.lianda.movies.R
import com.lianda.movies.base.BaseActivity
import com.lianda.movies.domain.model.Genre
import com.lianda.movies.presentation.adapter.GenreAdapter
import com.lianda.movies.presentation.movie.MovieListActivity
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.extentions.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var movieViewModel: MovieViewModel

    private var genreAdapter: GenreAdapter? = null

    override val layout: Int = R.layout.activity_main

    override fun onPreparation() {
        AndroidInjection.inject(this)

        if (genreAdapter == null) {
            val gridLayoutManager = GridLayoutManager(this@MainActivity, 2)
            genreAdapter = GenreAdapter(
                context = this,
                data = mutableListOf(),
                onGenreClicked = {
                    MovieListActivity.start(this, it)
                })

            rvMovies.apply {
                layoutManager = gridLayoutManager
                adapter = genreAdapter
            }
        }
    }

    override fun onIntent() {

    }

    override fun onUi() {
    }

    override fun onAction() {

    }

    override fun onObserver() {
        observeMovies()
    }

    private fun observeMovies() {
        observe(
            liveData = movieViewModel.fetchGenres(),
            action = ::manageStateGenre
        )
    }

    private fun manageStateGenre(result: ResultState<List<Genre>>) {
        when (result) {
            is ResultState.Success -> {
                msvMovie.showContentView()
                genreAdapter?.notifyDataAddOrUpdate(result.data)
            }
            is ResultState.Error -> {
                msvMovie.showErrorView(
                    icon = R.drawable.ic_movie_broken,
                    title = getString(R.string.label_oops),
                    message = result.throwable.message,
                    action = getString(R.string.action_retry),
                    actionListener = {
                        observeMovies()
                    }
                )
            }
            is ResultState.Loading -> {
                msvMovie.showLoadingView()
            }
            is ResultState.Empty -> {
                msvMovie.showEmptyView(
                    icon = R.drawable.ic_empty,
                    title = getString(R.string.label_oops),
                    message = getString(R.string.message_empty_movies)
                )
            }
        }
    }

}
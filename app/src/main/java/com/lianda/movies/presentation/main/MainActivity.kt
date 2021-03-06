package com.lianda.movies.presentation.main

import androidx.recyclerview.widget.GridLayoutManager
import com.lianda.movies.R
import com.lianda.movies.base.BaseActivity
import com.lianda.movies.databinding.ActivityMainBinding
import com.lianda.movies.domain.model.Genre
import com.lianda.movies.presentation.adapter.GenreAdapter
import com.lianda.movies.presentation.movie.MovieListActivity
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.extentions.*
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var movieViewModel: MovieViewModel

    private lateinit var binding: ActivityMainBinding

    private var genreAdapter: GenreAdapter? = null

    override fun onInflateView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        layout = binding.root
    }

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

            binding.adapter = genreAdapter
            binding.rvGenres.apply {
                layoutManager = gridLayoutManager
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
                binding.msvGenre.showContentView()
                genreAdapter?.notifyDataAddOrUpdate(result.data)
            }
            is ResultState.Error -> {
                binding.msvGenre.showErrorView(
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
                binding.msvGenre.showLoadingView()
            }
            is ResultState.Empty -> {
                binding.msvGenre.showEmptyView(
                    icon = R.drawable.ic_empty,
                    title = getString(R.string.label_oops),
                    message = getString(R.string.message_empty_movies)
                )
            }
        }
    }

}
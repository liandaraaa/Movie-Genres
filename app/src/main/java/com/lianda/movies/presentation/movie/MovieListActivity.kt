package com.lianda.movies.presentation.movie

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.lianda.movies.R
import com.lianda.movies.base.BaseActivity
import com.lianda.movies.base.BaseEndlessRecyclerViewAdapter
import com.lianda.movies.domain.model.EndlessMovie
import com.lianda.movies.presentation.adapter.MovieAdapter
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.constants.AppConstants.KEY_GENRE
import com.lianda.movies.utils.extentions.*
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListActivity : BaseActivity(), BaseEndlessRecyclerViewAdapter.OnLoadMoreListener {

    companion object {
        @JvmStatic
        fun start(context: Context, genreId: Int) {
            val starter = Intent(context, MovieListActivity::class.java)
                .putExtra(KEY_GENRE, genreId)
            context.startActivity(starter)
        }
    }

    private val movieViewModel: MovieViewModel by viewModel()

    private var movieAdapter: MovieAdapter? = null

    private var isLoadMore = false

    private var currentPage = 1
    private var totalPages = 0

    private var genreId: Int = 0

    override val layout: Int = R.layout.activity_movie_list

    override fun onPreparation() {
        if (movieAdapter == null) {
            val gridLayoutManager = GridLayoutManager(this, 2)
            movieAdapter = MovieAdapter(this, mutableListOf()) { movie ->
                MovieDetailActivity.start(this, movie)
            }
            movieAdapter?.apply {
                page = currentPage
                totalPage = totalPages
                layoutManager = gridLayoutManager
                onLoadMoreListener = this@MovieListActivity
                recyclerView = rvMovies
            }

            rvMovies.apply {
                layoutManager = gridLayoutManager
                adapter = movieAdapter
            }
        }
    }

    override fun onIntent() {
        genreId = intent.getIntExtra(KEY_GENRE, 0)
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
            liveData = movieViewModel.fetchMovies(currentPage, genreId.toString()),
            action = ::manageStateMovie
        )
    }

    private fun manageStateMovie(result: ResultState<EndlessMovie>) {
        when (result) {
            is ResultState.Success -> {
                msvMovie.showContentView()
                isLoadMore = false
                movieAdapter?.setLoadMoreProgress(false)
                totalPages = result.data.totalPage
                movieAdapter?.totalPage = totalPages
                movieAdapter?.notifyAddOrUpdateChanged(result.data.movies)
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
                isLoadMore = true
                movieAdapter?.setLoadMoreProgress(true)
            }
            is ResultState.Empty -> {
                if (isLoadMore) {
                    isLoadMore = false
                    movieAdapter?.setLoadMoreProgress(false)
                    movieAdapter?.removeScrollListener()
                } else {
                    movieAdapter?.datas?.clear()
                    msvMovie.showEmptyView(
                        icon = R.drawable.ic_empty,
                        title = getString(R.string.label_oops),
                        message = getString(R.string.message_empty_movies)
                    )
                }
            }
        }
    }

    override fun onLoadMore() {
        isLoadMore = true
        movieAdapter?.setLoadMoreProgress(true)
        currentPage += 1
        movieAdapter?.page = currentPage
        observeMovies()
    }

}
package com.lianda.movies.presentation.review

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lianda.movies.R
import com.lianda.movies.base.BaseActivity
import com.lianda.movies.base.BaseEndlessRecyclerViewAdapter
import com.lianda.movies.domain.model.EndlessReview
import com.lianda.movies.presentation.adapter.ReviewAdapter
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.constants.AppConstants.KEY_MOVIE
import com.lianda.movies.utils.extentions.*
import kotlinx.android.synthetic.main.activity_review_list.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewListActivity : BaseActivity(), BaseEndlessRecyclerViewAdapter.OnLoadMoreListener {
    companion object {
        @JvmStatic
        fun start(context: Context, movieId: Int) {
            val starter = Intent(context, ReviewListActivity::class.java)
                .putExtra(KEY_MOVIE, movieId)
            context.startActivity(starter)
        }
    }

    private val movieViewModel: MovieViewModel by viewModel()

    private var reviewAdapter: ReviewAdapter? = null

    private var isLoadMore = false

    private var currentPage = 1
    private var totalPages = 0

    private var movieId = 0

    override val layout: Int = R.layout.activity_review_list

    override fun onPreparation() {
        if (reviewAdapter == null) {
            val gridLayoutManager = LinearLayoutManager(this)
            reviewAdapter = ReviewAdapter(this, mutableListOf())
            reviewAdapter?.apply {
                page = currentPage
                totalPage = totalPages
                layoutManager = gridLayoutManager
                onLoadMoreListener = this@ReviewListActivity
                recyclerView = rvReview
            }

            rvReview.apply {
                layoutManager = gridLayoutManager
                adapter = reviewAdapter
            }
        }
    }

    override fun onIntent() {
        movieId = intent.getIntExtra(KEY_MOVIE, 0)
    }

    override fun onUi() {
        setupToolbar(toolbar,getString(R.string.label_review),true)
    }

    override fun onAction() {
    }

    override fun onObserver() {
        observeReviews()
    }

    private fun observeReviews() {
        observe(
            liveData = movieViewModel.fetchReviews(movieId = movieId, page = currentPage),
            action = ::manageStateReview
        )
    }

    private fun manageStateReview(result: ResultState<EndlessReview>) {
        when (result) {
            is ResultState.Success -> {
                msvReview.showContentView()
                isLoadMore = false
                reviewAdapter?.setLoadMoreProgress(false)
                totalPages = result.data.totalPage
                reviewAdapter?.totalPage = totalPages
                reviewAdapter?.notifyAddOrUpdateChanged(result.data.reviews)
            }
            is ResultState.Error -> {
                msvReview.showErrorView(
                    icon = R.drawable.ic_movie_broken,
                    title = getString(R.string.label_oops),
                    message = result.throwable.message,
                    action = getString(R.string.action_retry),
                    actionListener = {
                        observeReviews()
                    }
                )
            }
            is ResultState.Loading -> {
                msvReview.showLoadingView()
                isLoadMore = true
                reviewAdapter?.setLoadMoreProgress(true)
            }
            is ResultState.Empty -> {
                if (isLoadMore) {
                    isLoadMore = false
                    reviewAdapter?.setLoadMoreProgress(false)
                    reviewAdapter?.removeScrollListener()
                } else {
                    reviewAdapter?.datas?.clear()
                    msvReview.showEmptyView(
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
        reviewAdapter?.setLoadMoreProgress(true)
        currentPage += 1
        reviewAdapter?.page = currentPage
        observeReviews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }


}
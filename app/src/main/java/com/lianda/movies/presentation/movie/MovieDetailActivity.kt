package com.lianda.movies.presentation.movie

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.lianda.movies.R
import com.lianda.movies.base.BaseActivity
import com.lianda.movies.databinding.ActivityMovieDetailBinding
import com.lianda.movies.domain.model.EndlessReview
import com.lianda.movies.domain.model.Movie
import com.lianda.movies.domain.model.Review
import com.lianda.movies.domain.model.Video
import com.lianda.movies.presentation.adapter.ReviewAdapter
import com.lianda.movies.presentation.review.ReviewListActivity
import com.lianda.movies.presentation.viewmodel.MovieViewModel
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.constants.AppConstants.KEY_MOVIE
import com.lianda.movies.utils.extentions.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.android.AndroidInjection
import javax.inject.Inject

class MovieDetailActivity : BaseActivity() {

    companion object {
        fun start(context: Context, movie: Movie) {
            val starter = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra(KEY_MOVIE, movie)
            }
            context.startActivity(starter)
        }
    }

    @Inject
    lateinit var movieViewModel: MovieViewModel

    private lateinit var binding: ActivityMovieDetailBinding

    private val reviewAdapter: ReviewAdapter by lazy { ReviewAdapter(this, mutableListOf(), true) }

    private var movie: Movie? = null

    private var reviewPage = 1

    override fun onInflateView() {
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        layout = binding.root
    }

    override fun onPreparation() {
        AndroidInjection.inject(this)
    }

    override fun onIntent() {
        movie = intent.getParcelableExtra(KEY_MOVIE)
    }

    override fun onUi() {
        setupToolbar(binding.toolbar, movie?.title ?: getString(R.string.label_movie), true)
        binding.collapsingToolbar.title = movie?.title ?: getString(R.string.label_movie)
        showMovie()
    }

    override fun onAction() {
        binding.btnViewAllReview.setOnClickListener {
            movie?.let { movie ->
                ReviewListActivity.start(this, movie.id)
            }
        }
    }

    override fun onObserver() {
        observeMovieDetail()
        observeVideoTrailer()
        observeReviews()
    }

    private fun showMovie() {
        movie?.apply {
            binding.imgMovie.loadImage(posterPath, binding.pbPoster)
            binding.tvTitle.text = title
            binding.tvVote.text = voteAverage.toString()
            binding.ratMovie.rating = voteAverage.toFloat().div(2)
            binding.tvDate.text = releaseDate
        }
    }

    private fun showMovieDetail(data: Movie) {
        data.apply {
            binding.tvOriginalLanguage.text = originalLanguage
            binding.tvStatus.text = status
            binding.tvDuration.text = runtime.toReadableMinutes()
            binding.tvGenre.text = genres
            binding.tvDescription.text = overview
        }
    }

    private fun showVideoTrailer(data: Video) {
        if (data.youtubeKey.isNotEmpty()) {
            lifecycle.addObserver(binding.pvTrailer)
            binding.pvTrailer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(data.youtubeKey, 0f)
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    super.onError(youTubePlayer, error)
                    onVideoTrailerError()
                }
            })
        } else {
            onVideoTrailerEmpty()
        }
    }

    private fun showReviews(data: List<Review>) {
        reviewAdapter.notifyAddOrUpdateChanged(listOf(data.first()))
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(this@MovieDetailActivity)
            adapter = reviewAdapter
        }
    }

    private fun observeMovieDetail() {
        movie?.let { movie ->
            observe(
                liveData = movieViewModel.fetchMovieDetail(movie.id),
                action = ::manageStateMovie
            )
        }
    }

    private fun observeVideoTrailer() {
        movie?.let { movie ->
            observe(
                liveData = movieViewModel.fetchVideoTrailer(movie.id),
                action = ::manageStateVideoTrailer
            )
        }
    }

    private fun observeReviews() {
        movie?.let { movie ->
            observe(
                liveData = movieViewModel.fetchReviews(movie.id, reviewPage),
                action = ::manageStateReviews
            )
        }
    }

    private fun manageStateMovie(result: ResultState<Movie>) {
        when (result) {
            is ResultState.Success -> {
                binding.msvMovieDetail.showContentView()
                showMovieDetail(result.data)
            }
            is ResultState.Error -> {
                onMovieDetailError(result.throwable.message.toString())
            }
            is ResultState.Loading -> {
                binding.msvMovieDetail.showLoadingView()
            }
            is ResultState.Empty -> {
            }
        }
    }

    private fun manageStateVideoTrailer(result: ResultState<Video>) {
        when (result) {
            is ResultState.Success -> {
                binding.msvTrailer.showContentView()
                showVideoTrailer(result.data)
            }
            is ResultState.Error -> {
                onVideoTrailerError()
            }
            is ResultState.Loading -> {
                binding.msvTrailer.showLoadingView()
            }
            is ResultState.Empty -> {
            }
        }
    }

    private fun manageStateReviews(result: ResultState<EndlessReview>) {
        when (result) {
            is ResultState.Success -> {
                binding.msvReview.showContentView()
                binding.btnViewAllReview.visible()
                showReviews(result.data.reviews)
            }
            is ResultState.Error -> {
                binding.btnViewAllReview.gone()
                onReviewsError(result.throwable.message.toString())
            }
            is ResultState.Loading -> {
                binding.btnViewAllReview.gone()
                binding.msvReview.showLoadingView()
            }
            is ResultState.Empty -> {
                binding.btnViewAllReview.gone()
                onReviewsEmpty()
            }
        }
    }

    private fun onReviewsEmpty() {
        binding.msvReview.showEmptyView(
            icon = R.drawable.ic_empty,
            title = getString(R.string.label_oops),
            message = getString(R.string.message_empty_reviews)
        )
    }

    private fun onReviewsError(message: String) {
        binding.msvReview.showErrorView(
            icon = R.drawable.ic_movie_broken,
            title = getString(R.string.label_oops),
            message = message,
            action = getString(R.string.action_retry),
            actionListener = {
                observeReviews()
            }
        )
    }

    private fun onMovieDetailError(message: String) {
        binding.msvMovieDetail.showErrorView(
            icon = R.drawable.ic_movie_broken,
            title = getString(R.string.label_oops),
            message = message,
            action = getString(R.string.action_retry),
            actionListener = {
                observeMovieDetail()
            }
        )
    }

    private fun onVideoTrailerError() {
        binding.msvTrailer.showErrorView(
            icon = R.drawable.ic_movie_broken,
            title = getString(R.string.label_oops),
            message = getString(R.string.message_error_founded),
            action = getString(R.string.action_retry),
            actionListener = {
                observeVideoTrailer()
            }
        )
    }

    private fun onVideoTrailerEmpty() {
        binding.msvTrailer.showEmptyView(
            icon = R.drawable.ic_empty,
            title = getString(R.string.label_oops),
            message = getString(R.string.message_empty_videos)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}
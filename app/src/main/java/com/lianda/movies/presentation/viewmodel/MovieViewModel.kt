package com.lianda.movies.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lianda.movies.domain.model.*
import com.lianda.movies.domain.repository.MovieRepository
import com.lianda.movies.domain.usecase.MovieUseCase
import com.lianda.movies.utils.common.ResultState
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel(),
    MovieUseCase {

    private val fetchGenres = MutableLiveData<ResultState<List<Genre>>>()
    private val fetchMovies = MutableLiveData<ResultState<EndlessMovie>>()
    private val fetchMovieDetail = MutableLiveData<ResultState<Movie>>()
    private val fetchVideoTrailer = MutableLiveData<ResultState<Video>>()
    private val fetchReviews = MutableLiveData<ResultState<EndlessReview>>()


    init {
        fetchGenres.value = ResultState.Loading()
        fetchMovies.value = ResultState.Loading()
        fetchMovieDetail.value = ResultState.Loading()
        fetchVideoTrailer.value = ResultState.Loading()
        fetchReviews.value = ResultState.Loading()
    }

    override fun fetchGenres(): LiveData<ResultState<List<Genre>>> {
        viewModelScope.launch {
            val genreResponse = repository.fetchOfficialGenres()
            fetchGenres.value = genreResponse

        }
        return fetchGenres
    }

    override fun fetchMovies(page: Int, genres: String): LiveData<ResultState<EndlessMovie>> {
        viewModelScope.launch {
            when (val movieResponse = repository.fetchMovies(page, genres)) {
                is ResultState.Success -> {
                    val data = movieResponse.data.movies
                    if (data.isEmpty()) {
                        fetchMovies.value = ResultState.Empty()
                    } else {
                        fetchMovies.value = ResultState.Success(movieResponse.data)
                    }
                }
                else -> fetchMovies.value = movieResponse
            }
        }
        return fetchMovies
    }

    override fun fetchMovieDetail(movieId: Int): LiveData<ResultState<Movie>> {
        viewModelScope.launch {
            val movieDetailResponse = repository.fetchMovieDetail(movieId)
            fetchMovieDetail.value = movieDetailResponse
        }
        return fetchMovieDetail
    }

    override fun fetchVideoTrailer(movieId: Int): LiveData<ResultState<Video>> {
        viewModelScope.launch {
            val videoTrailerResponse = repository.fetchVideoTrailer(movieId)
            fetchVideoTrailer.value = videoTrailerResponse
        }
        return fetchVideoTrailer
    }

    override fun fetchReviews(movieId: Int, page: Int): LiveData<ResultState<EndlessReview>> {
        viewModelScope.launch {
            when (val reviewsResponse = repository.fetchReviews(movieId, page)) {
                is ResultState.Success -> {
                    val data = reviewsResponse.data.reviews
                    if (data.isEmpty()) {
                        fetchReviews.value = ResultState.Empty()
                    } else {
                        fetchReviews.value = ResultState.Success(reviewsResponse.data)
                    }
                }
                else -> fetchReviews.value = reviewsResponse
            }
        }
        return fetchReviews
    }

}
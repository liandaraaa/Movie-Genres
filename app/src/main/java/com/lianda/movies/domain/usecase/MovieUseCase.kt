package com.lianda.movies.domain.usecase

import androidx.lifecycle.LiveData
import com.lianda.movies.domain.model.*
import com.lianda.movies.utils.common.ResultState

interface MovieUseCase {

    fun fetchGenres():LiveData<ResultState<List<Genre>>>
    fun fetchMovies(page: Int,genres:String): LiveData<ResultState<EndlessMovie>>
    fun fetchMovieDetail(movieId: Int): LiveData<ResultState<Movie>>
    fun fetchVideoTrailer(movieId: Int): LiveData<ResultState<Video>>
    fun fetchReviews(movieId: Int,page: Int):LiveData<ResultState<EndlessReview>>

}
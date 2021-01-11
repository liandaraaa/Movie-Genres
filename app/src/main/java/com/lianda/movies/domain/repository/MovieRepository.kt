package com.lianda.movies.domain.repository

import com.lianda.movies.domain.model.*
import com.lianda.movies.utils.common.ResultState

interface MovieRepository {

    suspend fun fetchOfficialGenres():ResultState<List<Genre>>
    suspend fun fetchMovies(page:Int,genres:String) : ResultState<EndlessMovie>
    suspend fun fetchMovieDetail(movieId:Int) : ResultState<Movie>
    suspend fun fetchVideoTrailer(movieId: Int) : ResultState<Video>
    suspend fun fetchReviews(movieId: Int,page: Int) : ResultState<EndlessReview>

}
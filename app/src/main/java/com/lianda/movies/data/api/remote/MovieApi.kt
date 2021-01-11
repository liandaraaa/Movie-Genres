package com.lianda.movies.data.api.remote

import com.lianda.movies.data.api.sourceapi.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("genre/movie/list")
    suspend fun fetchOfficialGenres(@Query("api_key") apiKey: String = "306ccc27807f6de9adbf95b869018380"): Response<GenreSourceApiList>

    @GET("discover/movie")
    suspend fun fetchMovies(
        @Query("page") page: Int,
        @Query("with_genres") genres: String
    ): Response<MovieListSourceApi>

    @GET("movie/{id}")
    suspend fun fetchMovieDetail(@Path("id") movieId: Int): Response<MovieSourceApi>

    @GET("movie/{id}/videos")
    suspend fun fetchVideos(@Path("id") movieId: Int): Response<VideoListSourceApi>

    @GET("movie/{id}/reviews")
    suspend fun fetchReviews(@Path("id") movieId: Int): Response<ReviewListSourceApi>

}
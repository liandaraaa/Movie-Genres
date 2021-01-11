package com.lianda.movies.data.repository

import com.lianda.movies.data.api.remote.MovieApi
import com.lianda.movies.domain.model.*
import com.lianda.movies.domain.repository.MovieRepository
import com.lianda.movies.utils.common.ResultState
import com.lianda.movies.utils.extentions.handleApiError
import com.lianda.movies.utils.extentions.handleApiSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl (private val api: MovieApi): MovieRepository {

    override suspend fun fetchOfficialGenres(): ResultState<List<Genre>> {
        return try {
            val response = api.fetchOfficialGenres()
            if (response.isSuccessful){
                response.body()?.let {
                    withContext(Dispatchers.IO){
                        handleApiSuccess(data = it.genres?.map { it.toGenre() }.orEmpty())
                    }
                } ?: handleApiError(response)
            }else{
                handleApiError(response)
            }
        }catch (e: Exception){
            ResultState.Error(e)
        }
    }

    override suspend fun fetchMovies(page:Int,genres:String): ResultState<EndlessMovie> {
        return try {
            val response = api.fetchMovies(page,genres)
            if (response.isSuccessful){
                response.body()?.let {
                    withContext(Dispatchers.IO){
                        val endlessMovie = EndlessMovie(
                            totalPage = it.totalPages ?: 0,
                            movies =  it.movieSourceApis?.map { it.toMovie() }.orEmpty()
                        )
                        handleApiSuccess(data = endlessMovie)
                    }
                } ?: handleApiError(response)
            }else{
                handleApiError(response)
            }
        }catch (e: Exception){
            ResultState.Error(e)
        }
    }

    override suspend fun fetchMovieDetail(movieId: Int): ResultState<Movie> {
        return try {
            val response = api.fetchMovieDetail(movieId)
            if (response.isSuccessful){
                response.body()?.let {
                    withContext(Dispatchers.IO){
                        handleApiSuccess(data = it.toMovie())
                    }
                } ?: handleApiError(response)
            }else{
                handleApiError(response)
            }
        }catch (e: Exception){
            ResultState.Error(e)
        }
    }

    override suspend fun fetchVideoTrailer(movieId: Int): ResultState<Video> {
        return try {
            val response = api.fetchVideos(movieId)
            if (response.isSuccessful){
                response.body()?.let {
                    withContext(Dispatchers.IO){
                        val trailer = it.videoSourceApis?.find { it.type == "Trailer" }
                        handleApiSuccess(data = trailer?.toVideo() ?: Video())
                    }
                } ?: handleApiError(response)
            }else{
                handleApiError(response)
            }
        }catch (e: Exception){
            ResultState.Error(e)
        }
    }

    override suspend fun fetchReviews(movieId: Int): ResultState<List<Review>> {
        return try {
            val response = api.fetchReviews(movieId)
            if (response.isSuccessful){
                response.body()?.let {
                    withContext(Dispatchers.IO){
                        handleApiSuccess(data = it.reviewSourceApis?.map { it.toReview() }.orEmpty())
                    }
                } ?: handleApiError(response)
            }else{
                handleApiError(response)
            }
        }catch (e: Exception){
            ResultState.Error(e)
        }
    }

}
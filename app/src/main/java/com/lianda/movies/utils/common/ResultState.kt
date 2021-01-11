package com.lianda.movies.utils.common

sealed class ResultState<out T> {
    class Loading<out T> : ResultState<T>()
    class Empty<out T> : ResultState<T>()
    class Success<out T>(val data: T) : ResultState<T>()
    class Error(val throwable: Throwable) : ResultState<Nothing>()
}
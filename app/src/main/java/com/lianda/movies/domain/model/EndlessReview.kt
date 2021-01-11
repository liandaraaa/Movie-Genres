package com.lianda.movies.domain.model

data class EndlessReview(
    val totalPage:Int = 0,
    val reviews:List<Review> = listOf()
)
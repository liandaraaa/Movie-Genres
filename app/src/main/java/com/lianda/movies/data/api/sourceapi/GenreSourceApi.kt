package com.lianda.movies.data.api.sourceapi


import com.google.gson.annotations.SerializedName
import com.lianda.movies.domain.model.Genre

data class GenreSourceApiList(
    @SerializedName("genres")
    val genres:List<GenreSourceApi>?
)

data class GenreSourceApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
){
    fun toGenre():Genre{
        return Genre(
            id = id ?: 0,
            name = name.orEmpty()
        )
    }
}
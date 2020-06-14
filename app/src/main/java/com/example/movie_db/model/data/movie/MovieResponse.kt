package com.example.movie_db.model.data.movie

import com.example.movie_db.model.data.movie.Movie
import com.google.gson.annotations.SerializedName

class MovieResponse {

    @SerializedName("results")
    private val results: List<Movie>? = null

    @SerializedName("page")
    private val page: Int = 1

    fun getResults(): List<Movie>? {
        return results
    }
}

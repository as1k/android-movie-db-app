package com.example.movie_db.model.data.movie

import com.example.movie_db.model.data.movie.Movie
import com.google.gson.annotations.SerializedName

class MovieResponse {

    @SerializedName("results")
    private val results: List<Movie>? = null

    fun getResults(): List<Movie>? {
        return results
    }
}
